package com.blackstar.softwarelab.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.blackstar.softwarelab.bean.ContainerInfo;
import com.blackstar.softwarelab.common.ContainerStatusConst;
import com.blackstar.softwarelab.common.DbConst;
import com.blackstar.softwarelab.entity.Instance;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@ConditionalOnProperty(prefix = "checker", value = "enabled", havingValue = "true")
public class ContainerChecker {

    @Value("${checker.sleepMillSeconds:500}")
    private int sleepMillSeconds;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private IInstanceService instanceService;


    private ExecutorService repairThreadPool = Executors.newFixedThreadPool(10);

    private Map<String, Future> needRepairContainersMap = new ConcurrentHashMap<>();

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        new Thread(() -> {
            try {
                for (; ; ) {
                    checkContainerStatus();
                    checkRepairMap();
                    TimeUnit.MILLISECONDS.sleep(sleepMillSeconds);
                }
            } catch (InterruptedException e) {
                log.error("checker Interrupted", e);
            }
        }, "checkStartThread").start();

    }

    private void checkContainerStatus() {
        List<Instance> instances = instanceService.list(new QueryWrapper<Instance>()
                .eq(DbConst.COLUMN_RUNNING_STATUS, DbConst.RUNNING_STATUS_START));
        if (instances == null || instances.size() == 0) {
            return;
        }
        List<String> instanceIds = instances.parallelStream().map(Instance::getId).collect(Collectors.toList());
        List<ContainerInfo> containerInfos = containerService.listByNames(instanceIds);
        Map<String, ContainerInfo> nameContainerInfoHashMap = null;
        if (containerInfos != null && containerInfos.size() > 0) {
            nameContainerInfoHashMap = containerInfos.parallelStream()
                    .filter(containerInfo -> containerInfo.getStatus().equals(ContainerStatusConst.RUNNING)
                            || containerInfo.getStatus().equals(ContainerStatusConst.RESTARTING))
                    .collect(Collectors.toMap(ContainerInfo::getName, ContainerInfo::self));
        }
        Map<String, ContainerInfo> finalNameContainerInfoHashMap = nameContainerInfoHashMap;
        instances.parallelStream().forEach(instance -> {
            //don't add to needRepairContainersMap if exist
            if ((finalNameContainerInfoHashMap == null || finalNameContainerInfoHashMap.get(instance.getId()) == null) && needRepairContainersMap.get(instance.getId()) == null) {
                needRepairContainersMap.put(instance.getId(), repairThreadPool.submit(() -> {
                    instanceService.startByInstance(instance);
                }));
            }
        });
    }

    private void checkRepairMap() {
        needRepairContainersMap.forEach((instanceId, future) -> {
            if (future.isDone()) {
                switch (containerService.getState(instanceId)) {
                    case ContainerStatusConst.CREATED:
                    case ContainerStatusConst.RESTARTING:
                    case ContainerStatusConst.REMOVING:
                    case ContainerStatusConst.EXITED:
                        //just wait，do nothing
                        break;
                    case ContainerStatusConst.RUNNING:
                        //remove from map
                        needRepairContainersMap.remove(instanceId);
                        break;
                    case ContainerStatusConst.PAUSED:
                    case ContainerStatusConst.DEAD:
                        //remove container in docker
                        Instance instance = instanceService.getById(instanceId);
                        if (instance == null) {
                            log.error("instance {} is dead or paused ,but is removed", instanceId);
                            return;
                        }
                        try {
                            ContainerInfo containerInfo = objectMapper.readValue(instance.getAdditionalInfo(), ContainerInfo.class);
                            containerService.remove(containerInfo);
                            //remove from map
                            needRepairContainersMap.remove(instanceId);
                        } catch (IOException e) {
                            log.error("read value from instance failed", e);
                        }
                        break;
                    default:

                        break;
                }
            }
        });
    }

}

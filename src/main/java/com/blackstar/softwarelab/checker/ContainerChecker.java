package com.blackstar.softwarelab.checker;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.blackstar.softwarelab.bean.ContainerInfo;
import com.blackstar.softwarelab.common.ContainerStatusConst;
import com.blackstar.softwarelab.common.DbConst;
import com.blackstar.softwarelab.entity.Instance;
import com.blackstar.softwarelab.service.ContainerService;
import com.blackstar.softwarelab.service.IInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;


@Component
@Slf4j
@ConditionalOnProperty(prefix = "softwarelab.checker", value = "enabled", havingValue = "true")
public class ContainerChecker {

    @Value("${softwarelab.checker.sleepMillSeconds:500}")
    private int sleepMillSeconds;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private IInstanceService instanceService;


    private ExecutorService repairThreadPool = Executors.newFixedThreadPool(10);

    private Map<String, Future> repairingContainersMap = new ConcurrentHashMap<>();

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
        }, "checkContainerThread").start();

    }

    private void checkContainerStatus() {
        List<Instance> instances = instanceService.list(new QueryWrapper<Instance>()
                .eq(DbConst.COLUMN_RUNNING_STATUS, DbConst.RUNNING_STATUS_START));
        if (instances == null || instances.size() == 0) {
            return;
        }
        List<String> instanceIds = instances.parallelStream().map(Instance::getId).collect(Collectors.toList());
        List<ContainerInfo> containerInfos = containerService.listByNames(instanceIds);
        final Map<String, ContainerInfo> finalNameContainerInfoMap = containerInfos == null ? new ConcurrentHashMap<>() : containerInfos.parallelStream().collect(Collectors.toMap(ContainerInfo::getName, ContainerInfo::self));
        instances.parallelStream().forEach(instance -> {
            if (repairingContainersMap.get(instance.getId()) != null) {
                return;
            }
            ContainerInfo containerInfo = finalNameContainerInfoMap.get(instance.getId());
            if (containerInfo != null) {
                switch (containerInfo.getStatus()) {
                    case ContainerStatusConst.RESTARTING:
                    case ContainerStatusConst.RUNNING:
                        // begin removing,just wait
                    case ContainerStatusConst.REMOVING:
                        break;
                    case ContainerStatusConst.CREATED:
                    case ContainerStatusConst.EXITED:
                        //need to start
                        log.warn("instance id:[{}] begin to repair",instance.getId());
                        repairingContainersMap.put(instance.getId(), repairThreadPool.submit(() -> {
                            instanceService.startByInstance(instance);
                        }));
                        break;
                    //unused status
                    case ContainerStatusConst.PAUSED:
                    case ContainerStatusConst.DEAD:
                        //remove and wait next schedule
                        containerService.removeById(containerInfo.getId());
                    default:
                        break;
                }

            } else {
                //need to start
                log.warn("instance id:[{}] begin to repair",instance.getId());
                repairingContainersMap.put(instance.getId(), repairThreadPool.submit(() -> {
                    instanceService.startByInstance(instance);
                }));
            }
        });
    }

    private void checkRepairMap() {
        repairingContainersMap.forEach((instanceId, future) -> {
            if (future.isDone()) {
                switch (containerService.getState(instanceId)) {
                    case ContainerStatusConst.RUNNING:
                        //remove from map
                        repairingContainersMap.remove(instanceId);
                        break;
                    default:
                        break;
                }
            }
        });
    }

}

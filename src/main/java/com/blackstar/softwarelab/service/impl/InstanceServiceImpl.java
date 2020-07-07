package com.blackstar.softwarelab.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blackstar.softwarelab.bean.ContainerInfo;
import com.blackstar.softwarelab.bean.ContainerSetting;
import com.blackstar.softwarelab.common.DbConst;
import com.blackstar.softwarelab.entity.App;
import com.blackstar.softwarelab.entity.Instance;
import com.blackstar.softwarelab.entity.SysUser;
import com.blackstar.softwarelab.mapper.InstanceMapper;
import com.blackstar.softwarelab.security.SecurityUser;
import com.blackstar.softwarelab.service.ContainerService;
import com.blackstar.softwarelab.service.IAppService;
import com.blackstar.softwarelab.service.IInstanceService;
import com.blackstar.softwarelab.service.ISysUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author blackstar
 * @since 2020-03-27
 */
@Service
public class InstanceServiceImpl extends ServiceImpl<InstanceMapper, Instance> implements IInstanceService {


    @Autowired
    private ContainerService containerService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private IAppService appService;

    @Value("${instance.ports}")
    private String ports;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean start(String userId, String instanceId) {
        Instance instance = this.getById(instanceId);
        if (instance == null) {
            throw new RuntimeException("can't find instance info by instance");
        }
        return startByInstance(instance);
    }


    public boolean startByInstance(Instance instance) {
        String additionalInfo = instance.getAdditionalInfo();
        if (additionalInfo == null) {
            throw new RuntimeException("can't find container info by instance");
        } else {
            try {
                ContainerInfo containerInfo = objectMapper.readValue(additionalInfo, ContainerInfo.class);
                //解决与ContainerChecker修复冲突，start时确保instance相关容器启动完成后，才设置状态为RUNNING_STATUS_START，stop时，先设置RUNNING_STATUS_STOP状态，再stop容器
                //TODO 需要check等待容器启动完毕后再写入状态
                UpdateWrapper<Instance> wrapper = new UpdateWrapper<Instance>()
                        .set(DbConst.COLUMN_RUNNING_STATUS,DbConst.RUNNING_STATUS_START)
                        .set(DbConst.COLUMN_ADDITIONAL_INFO, objectMapper.writeValueAsString(containerService.start(containerInfo)))
                        .eq(DbConst.COLUMN_ID, instance.getId());
                return this.update(wrapper);
            } catch (IOException e) {
                throw new RuntimeException("instance start error", e);
            }
        }
    }

    @Override
    public boolean stop(SecurityUser securityUser, String id) {
        Instance instance = this.getById(id);
        if (instance == null) {
            throw new RuntimeException("can't find instance by id");
        }
        ContainerInfo containerInfo = getContainerInfo(instance);
        if (containerInfo == null) {
            throw new RuntimeException("container info is null");
        }
        UpdateWrapper<Instance> wrapper = new UpdateWrapper<Instance>()
                .set(DbConst.COLUMN_RUNNING_STATUS,DbConst.RUNNING_STATUS_START)
                .eq(DbConst.COLUMN_ID, instance.getId());
        boolean updateFlag  = this.update(wrapper);
        containerService.stop(containerInfo);
        return updateFlag;
    }


    @Override
    public void add(String userId, Instance instance) {
        SysUser user = userService.getById(userId);
        App app = appService.getById(instance.getAppName());
        instance.setId(UUID.randomUUID().toString());
        ContainerInfo containerInfo = generateContainerInfo(app, user, instance);
        try {
            instance.setAdditionalInfo(objectMapper.writeValueAsString(containerInfo));
            instance.setName(instance.getName());
            instance.setUserId(userId);
            LocalDateTime now = LocalDateTime.now();
            instance.setCreateTime(now);
            instance.setUpdateTime(now);
            this.save(instance);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean delete(String id) {
        Instance instance = this.getById(id);
        ContainerInfo containerInfo = getContainerInfo(instance);
        if (containerInfo.getId() != null) {
            containerService.remove(containerInfo);
        }
        return this.removeById(id);
    }

    private ContainerInfo getContainerInfo(Instance instance) {
        String additionalInfo = instance.getAdditionalInfo();
        if (additionalInfo == null) {
            return null;
        }
        try {
            return objectMapper.readValue(additionalInfo, ContainerInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ContainerInfo generateContainerInfo(App app, SysUser user, Instance instance) {
        ContainerInfo containerInfo = null;
        try {
            ContainerSetting containerSetting = objectMapper.readValue(app.getAdditionalInfo(), ContainerSetting.class);
            if (containerSetting == null) {
                return null;
            }
            containerInfo = objectMapper.readValue(instance.getAdditionalInfo(), ContainerInfo.class);
            //sys labels
            List<String> sysLabels = Arrays.asList("appName:" + app.getName(), "appVersion:" + instance.getAppVersion(), "userId:" + user.getId());
            if (containerInfo.getLabels() != null) {
                sysLabels.addAll(containerInfo.getLabels());
            }
            containerInfo = ContainerInfo.builder()
                    .imageName(containerSetting.getImageName())
                    .name(instance.getId())
                    .ports(containerInfo.getPorts())
                    .labels(sysLabels)
                    .envs(containerInfo.getEnvs())
                    .build();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return containerInfo;
    }

    private String generatorName(String username, String image) {
        StringBuilder sb = new StringBuilder();
        sb.append(username);
        String[] nameVersionStr = image.split(":");
        String[] names = nameVersionStr[0].split("/");
        if (names.length == 1) {
            sb.append(names[0]);
        } else {
            sb.append(names[0] + "_" + names[1]);
        }
        if (nameVersionStr.length > 1) {
            sb.append("_" + nameVersionStr[1]);
        }
        return sb.toString();
    }

}

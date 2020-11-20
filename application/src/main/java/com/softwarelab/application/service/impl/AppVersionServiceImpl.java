package com.softwarelab.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.softwarelab.application.bean.ContainerSetting;
import com.softwarelab.application.service.IAppService;
import com.softwarelab.application.entity.App;
import com.softwarelab.application.entity.AppVersion;
import com.softwarelab.application.mapper.AppVersionMapper;
import com.softwarelab.application.service.IAppVersionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author blackstar
 * @since 2020-06-27
 */
@Service
public class AppVersionServiceImpl extends ServiceImpl<AppVersionMapper, AppVersion> implements IAppVersionService {


    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private IAppService appService;


    private static final String APP_NAME = "app_name";

    private static final String VERSION = "version";

    @Override
    public List<AppVersion> getSimpleByAppName(String appName) {
        return this.baseMapper.getSimpleByAppName(appName);
    }

    @Override
    public List<AppVersion> getVersionsByAppName(String appName) {
        return this.list(new QueryWrapper<AppVersion>().eq(APP_NAME, appName));
    }

    @Override
    public AppVersion getVersionByNameAndVersion(String appName, String version) {
        AppVersion appVersion = this.getOne(new QueryWrapper<AppVersion>().eq(APP_NAME, appName).eq(VERSION, version));
        App app = appService.getByName(appName);
        try {
            if (appVersion.getAdditionalInfo() == null) {
                appVersion.setAdditionalInfo(app.getAdditionalInfo());
            } else {
                ContainerSetting appVersionContainerSetting = objectMapper.readValue(appVersion.getAdditionalInfo(), ContainerSetting.class);
                ContainerSetting appContainerSetting = objectMapper.readValue(app.getAdditionalInfo(), ContainerSetting.class);
                if (appVersionContainerSetting.getImageName() != null) {
                    appContainerSetting.setImageName(appVersionContainerSetting.getImageName());
                }
                if (appVersionContainerSetting.getPorts() != null) {
                    appContainerSetting.setPorts(appVersionContainerSetting.getPorts());
                }
                appVersion.setAdditionalInfo(objectMapper.writeValueAsString(appContainerSetting));
            }
        } catch (IOException e) {
            log.error("get version by id has error:", e);
        }
        return appVersion;
    }

}

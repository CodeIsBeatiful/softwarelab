package com.blackstar.softwarelab.service;

import com.blackstar.softwarelab.entity.AppVersion;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author blackstar
 * @since 2020-06-27
 */
public interface IAppVersionService extends IService<AppVersion> {

    List<AppVersion> getSimpleByAppName(String appName);

    List<AppVersion> getVersionsByAppName(String appName);

    AppVersion getVersionByNameAndVersion(String appName, String version);
}

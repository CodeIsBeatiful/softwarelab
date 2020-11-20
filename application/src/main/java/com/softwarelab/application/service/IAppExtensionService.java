package com.softwarelab.application.service;

import com.softwarelab.application.entity.AppExtension;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author blackstar
 * @since 2020-10-25
 */
public interface IAppExtensionService extends IService<AppExtension> {

    void addUsedCount(String appName);
}

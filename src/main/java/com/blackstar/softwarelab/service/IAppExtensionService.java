package com.blackstar.softwarelab.service;

import com.blackstar.softwarelab.entity.AppExtension;
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

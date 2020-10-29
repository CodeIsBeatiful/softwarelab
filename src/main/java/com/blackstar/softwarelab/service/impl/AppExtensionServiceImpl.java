package com.blackstar.softwarelab.service.impl;

import com.blackstar.softwarelab.common.DbConst;
import com.blackstar.softwarelab.entity.AppExtension;
import com.blackstar.softwarelab.mapper.AppExtensionMapper;
import com.blackstar.softwarelab.service.IAppExtensionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author blackstar
 * @since 2020-10-25
 */
@Service
public class AppExtensionServiceImpl extends ServiceImpl<AppExtensionMapper, AppExtension> implements IAppExtensionService {

    @Override
    public void addUsedCount(String appName) {
        LocalDateTime now = LocalDateTime.now();
        AppExtension appExtension = this.getById(appName);
        if (appExtension != null) {
            //
            appExtension.setUsedCount(appExtension.getUsedCount() + 1)
                    .setUpdateTime(now);
        } else {
            appExtension = AppExtension.builder()
                    .appName(appName)
                    .usedCount(1)
                    .createTime(now)
                    .updateTime(now)
                    .status(DbConst.STATUS_NORMAL)
                    .build();

        }
        this.saveOrUpdate(appExtension);
    }
}

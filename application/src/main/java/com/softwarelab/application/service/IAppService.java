package com.softwarelab.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.softwarelab.application.bean.AppInfo;
import com.softwarelab.application.entity.App;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author blackstar
 * @since 2020-03-28
 */
public interface IAppService extends IService<App> {

    List<String> getNameByType(String type);

    App getByName(String name);

    List<AppInfo> getTop(int topNumber);
}

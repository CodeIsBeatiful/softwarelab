package com.blackstar.softwarelab.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blackstar.softwarelab.bean.AppInfo;
import com.blackstar.softwarelab.entity.App;

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

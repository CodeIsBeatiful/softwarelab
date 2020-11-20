package com.softwarelab.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.softwarelab.application.bean.AppInfo;
import com.softwarelab.application.entity.App;
import com.softwarelab.application.mapper.AppMapper;
import com.softwarelab.application.service.IAppService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author blackstar
 * @since 2020-03-28
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements IAppService {


    @Override
    public List<String> getNameByType(String type) {
        return this.baseMapper.getNameByType(type);
    }

    @Override
    public App getByName(String name) {
        return this.getOne(new QueryWrapper<App>().eq("name",name));
    }

    @Override
    public List<AppInfo> getTop(int topNumber) {
        return this.baseMapper.getTop(topNumber);
    }
}

package com.blackstar.softwarelab.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blackstar.softwarelab.common.DbConst;
import com.blackstar.softwarelab.entity.App;
import com.blackstar.softwarelab.mapper.AppMapper;
import com.blackstar.softwarelab.service.IAppService;
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
}

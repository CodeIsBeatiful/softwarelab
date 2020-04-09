package com.blackstar.softwarelab.app.service.impl;

import com.blackstar.softwarelab.app.entity.App;
import com.blackstar.softwarelab.app.mapper.AppMapper;
import com.blackstar.softwarelab.app.service.IAppService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blackstar.softwarelab.common.KeyValuePair;
import com.blackstar.softwarelab.instance.bean.ContainerInfo;
import com.blackstar.softwarelab.instance.bean.ContainerSetting;
import com.blackstar.softwarelab.user.entity.SysUser;
import com.blackstar.softwarelab.user.service.ISysUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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





}

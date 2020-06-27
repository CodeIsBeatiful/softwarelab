package com.blackstar.softwarelab.service.impl;

import com.blackstar.softwarelab.entity.AppVersion;
import com.blackstar.softwarelab.mapper.AppVersionMapper;
import com.blackstar.softwarelab.service.IAppVersionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author blackstar
 * @since 2020-06-27
 */
@Service
public class AppVersionServiceImpl extends ServiceImpl<AppVersionMapper, AppVersion> implements IAppVersionService {

}

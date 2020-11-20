package com.softwarelab.application.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.softwarelab.application.entity.SysUser;
import com.softwarelab.application.mapper.SysUserMapper;
import com.softwarelab.application.service.ISysUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author blackstar
 * @since 2020-03-27
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

}

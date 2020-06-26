package com.blackstar.softwarelab.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blackstar.softwarelab.entity.SysUser;
import com.blackstar.softwarelab.mapper.SysUserMapper;
import com.blackstar.softwarelab.service.ISysUserService;
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

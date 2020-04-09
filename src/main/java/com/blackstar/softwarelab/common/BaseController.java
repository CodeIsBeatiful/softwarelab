package com.blackstar.softwarelab.common;

import com.blackstar.softwarelab.security.SecurityUser;
import com.blackstar.softwarelab.user.entity.SysUser;
import com.blackstar.softwarelab.user.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {


    @Autowired
    private ISysUserService userService;

    public SecurityUser getSecurityUser() {

        SysUser user = userService.getById("205635b9-ab37-43cb-82c2-811a58880fa1");

        return SecurityUser.builder()
                .id(user.getId())
                .mail(user.getMail())
                .username(user.getUsername())
                .build();
    }
}

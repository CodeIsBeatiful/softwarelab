package com.blackstar.softwarelab.controller;

import com.blackstar.softwarelab.bean.SecurityUser;
import com.blackstar.softwarelab.common.BaseController;
import com.blackstar.softwarelab.entity.SysUser;
import com.blackstar.softwarelab.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController extends BaseController {

    @Autowired
    private ISysUserService userService;

    @RequestMapping(value = "/user",method = RequestMethod.GET)
    public SysUser getUser(){
        SecurityUser securityUser = getSecurityUser();
        String id = securityUser.getId();
        return userService.getById(id);
    }


}

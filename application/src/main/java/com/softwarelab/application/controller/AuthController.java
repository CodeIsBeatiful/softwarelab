package com.softwarelab.application.controller;

import com.softwarelab.application.bean.SecurityUser;
import com.softwarelab.application.common.BaseController;
import com.softwarelab.application.entity.SysUser;
import com.softwarelab.application.service.ISysUserService;
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

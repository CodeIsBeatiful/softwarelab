package com.blackstar.softwarelab.controller;


import com.blackstar.softwarelab.bean.ContainerInfo;
import com.blackstar.softwarelab.common.BaseController;
import com.blackstar.softwarelab.security.SecurityUser;
import com.blackstar.softwarelab.service.IInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/instanceOperation")
public class InstanceOperationController extends BaseController {


    @Autowired
    private IInstanceService instanceService;



    @RequestMapping(method = RequestMethod.GET,value = "")
    public List<ContainerInfo> list(){
        SecurityUser securityUser = getSecurityUser();

        return null;
    }



    @RequestMapping(method = RequestMethod.POST,value = "{id}?op=start")
    public boolean start(@PathVariable String id){
        SecurityUser securityUser = getSecurityUser();

        return instanceService.start(securityUser.getId(),id);
    }


    @RequestMapping(method = RequestMethod.POST,value = "{id}?op=stop")
    public boolean stop(@PathVariable String id){
        SecurityUser securityUser = getSecurityUser();
        return instanceService.stop(securityUser.getId(),id);

    }







}

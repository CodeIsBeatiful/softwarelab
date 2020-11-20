package com.softwarelab.application.controller;


import com.softwarelab.application.bean.SecurityUser;
import com.softwarelab.application.common.BaseController;
import com.softwarelab.application.service.IInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/instanceOperation")
public class InstanceOperationController extends BaseController {


    @Autowired
    private IInstanceService instanceService;


    @RequestMapping(method = RequestMethod.POST, value = "/{id}")
    public boolean operate(@PathVariable String id, @RequestParam String op) {
        SecurityUser securityUser = getSecurityUser();
        if (op.equals("start")) {
            return instanceService.start(securityUser.getId(), id);
        } else if (op.equals("stop")) {
            return instanceService.stop(securityUser.getId(), id);
        } else {
            throw new RuntimeException("op is not supported");
        }

    }


}

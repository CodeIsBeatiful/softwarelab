package com.blackstar.softwarelab.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blackstar.softwarelab.bean.ContainerInfo;
import com.blackstar.softwarelab.common.BaseController;
import com.blackstar.softwarelab.common.DbConst;
import com.blackstar.softwarelab.entity.Instance;
import com.blackstar.softwarelab.bean.SecurityUser;
import com.blackstar.softwarelab.service.IInstanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author blackstar
 * @since 2020-03-27
 */
@RestController
@RequestMapping("/api/instances")
public class InstanceController extends BaseController {

    @Autowired
    private IInstanceService instanceService;


    private ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public Instance get(@PathVariable String id) {
        return instanceService.getById(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Instance add(@RequestBody Instance instance) {
        check(instance);
        SecurityUser securityUser = getSecurityUser();
        instanceService.add(securityUser.getId(), instance);
        return instance;
    }

    private void check(Instance instance) {
        try {
            if(instance.getAdditionalInfo() == null){
                throw new RuntimeException("instance additional info must not null");
            }
            ContainerInfo containerInfo = objectMapper.readValue(instance.getAdditionalInfo(), ContainerInfo.class);
            List<String> ports = containerInfo.getPorts();
            //check ports
            if(ports == null){
                throw new RuntimeException("container info mush have ports");
            }
        } catch (IOException e) {
            throw new RuntimeException("check container info error:", e);
        }
    }

//    @RequestMapping(method = RequestMethod.PUT)
//    public Instance update(Instance instance) {
//        instanceService.updateById(instance);
//        return instanceService.getById(instance.getId());
//    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public boolean delete(@PathVariable String id) {
        return instanceService.delete(id);
    }


    @RequestMapping(method = RequestMethod.GET)
    public IPage<Instance> list(@RequestParam int pageNum, @RequestParam int pageSize, String image, String name) {

        Page<Instance> instancePage = new Page<>(pageNum, pageSize);

        QueryWrapper<Instance> appQueryWrapper = new QueryWrapper<>();
        appQueryWrapper.eq(DbConst.COLUMN_STATUS, DbConst.STATUS_NORMAL);
        if (image != null) {
            appQueryWrapper.eq("app_name", image);
        }
        if (name != null) {
            appQueryWrapper.like("name", name);
        }
        return instanceService.page(instancePage, appQueryWrapper);
    }

}

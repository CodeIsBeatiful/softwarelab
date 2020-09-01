package com.blackstar.softwarelab.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blackstar.softwarelab.bean.*;
import com.blackstar.softwarelab.common.BaseController;
import com.blackstar.softwarelab.entity.Instance;
import com.blackstar.softwarelab.service.IAppVersionService;
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

    @Autowired
    private IAppVersionService appVersionService;

    private ObjectMapper objectMapper = new ObjectMapper();


    private static String[] canSortStr = {"updateTime", "createTime", "runningStatus", "name"};

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
            if (instance.getAdditionalInfo() == null) {
                throw new RuntimeException("instance additional info can't be null");
            }
            ContainerInfo containerInfo = objectMapper.readValue(instance.getAdditionalInfo(), ContainerInfo.class);
            //check ports
            List<ContainerPortSetting> ports = containerInfo.getPorts();
            if (ports == null) {
                throw new RuntimeException("container info ports can't be null");
            }
            // check envs
            List<ContainerEnvSetting> envs = containerInfo.getEnvs();
            for (ContainerEnvSetting env : envs) {
                if(env.getKey() == null || env.getValue() == null){
                    throw new RuntimeException("container info env key can't be null");
                }
            }
            if(appVersionService.getVersionByNameAndVersion(instance.getAppName(),instance.getAppVersion()) == null) {
                throw new RuntimeException("can't find "+ instance.getAppName()+":"+instance.getAppVersion());
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
    public IPage<Instance> list(@RequestParam int pageNum, @RequestParam int pageSize, String image, String name, String sort) {
        //add sort;
        // todo change property name image to app
        Page<Instance> instancePage = new Page<>(pageNum, pageSize);

        QueryWrapper<Instance> appQueryWrapper = new QueryWrapper<>();

        if (image != null) {
            appQueryWrapper.eq("app_name", image);
        }
        if (name != null) {
            appQueryWrapper.like("name", name);
        }
        SortObj sortObj = checkSort(canSortStr, sort);
        //
        processSortObjToWrapper(appQueryWrapper, sortObj);
        return instanceService.page(instancePage, appQueryWrapper);
    }

    private void processSortObjToWrapper(QueryWrapper<Instance> appQueryWrapper, SortObj sortObj) {
        if (sortObj == null) {
            appQueryWrapper.orderByDesc("update_time");
            return;
        }
        String order = sortObj.getOrder();
        if (order.equals("desc")) {
            appQueryWrapper.orderByDesc(sortObj.getValue());
        } else if (order.equals("asc")) {
            appQueryWrapper.orderByAsc(sortObj.getValue());
        } else {
            //do nothing
        }
    }


}

package com.blackstar.softwarelab.instance.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blackstar.softwarelab.common.DbConst;
import com.blackstar.softwarelab.instance.entity.Instance;
import com.blackstar.softwarelab.instance.service.IInstanceService;
import com.blackstar.softwarelab.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import com.blackstar.softwarelab.common.BaseController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author blackstar
 * @since 2020-03-27
 */
@Controller
@RequestMapping("/api/instances")
public class InstanceController extends BaseController {

    @Autowired
    private IInstanceService instanceService;

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public Instance get(@PathVariable String id) {
        return instanceService.getById(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Instance add(Instance instance) {
        SecurityUser securityUser = getSecurityUser();
        instanceService.add(securityUser.getId(),instance);
        return instance;
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

//    public boolean delete(@PathVariable String id){
//        return instanceService.removeById(id);
//    }


    @RequestMapping(method = RequestMethod.GET)
    public IPage<Instance> list(@RequestParam int pageNum, @RequestParam int pageSize) {

        Page<Instance> instancePage = new Page<>(pageNum, pageSize);

        QueryWrapper<Instance> appQueryWrapper = new QueryWrapper<>();
        appQueryWrapper.eq(DbConst.COLUMN_STATUS, DbConst.STATUS_NORMAL);

        return instanceService.page(instancePage, appQueryWrapper);
    }

}

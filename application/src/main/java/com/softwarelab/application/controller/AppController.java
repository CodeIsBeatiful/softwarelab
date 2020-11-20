package com.softwarelab.application.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.softwarelab.application.bean.AppInfo;
import com.softwarelab.application.common.BaseController;
import com.softwarelab.application.common.DbConst;
import com.softwarelab.application.entity.App;
import com.softwarelab.application.service.IAppService;
import com.softwarelab.application.service.IAppSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author blackstar
 * @since 2020-03-27
 */
@Slf4j
@RestController
@RequestMapping("/api/apps")
public class AppController extends BaseController {


    @Autowired
    private IAppService appService;

    @Autowired
    private IAppSourceService appSourceService;

    @RequestMapping(method = RequestMethod.GET, value = "/{name}")
    public App get(@PathVariable String name) {
        return appService.getById(name);
    }


    @RequestMapping(method = RequestMethod.GET)
    public IPage<App> list(@RequestParam int pageNum, @RequestParam int pageSize,@RequestParam(required = false) String type) {

        Page<App> appPage = new Page<>(pageNum, pageSize);
        QueryWrapper<App> appQueryWrapper = new QueryWrapper<App>();
        if(type != null && !type.equals("All")){
            appQueryWrapper= appQueryWrapper.eq(DbConst.COLUMN_TYPE, type);
        }
        return appService.page(appPage, appQueryWrapper);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/top/{number}")
    public List<AppInfo> top(@PathVariable int number) {
        if(number< 0 || number < 10 ){
             throw new RuntimeException("number must in 0 and 10");
        }
        return appService.getTop(number);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/names/{type}")
    public List<String> getNameByType(@PathVariable String type){
        if("All".equals(type)){
            type = null;
        }
        return appService.getNameByType(type);
    }



    @RequestMapping(method = RequestMethod.POST, value = "?op=upgrade")
    public boolean upgrade() {
        return appSourceService.upgrade();
    }

    @RequestMapping(method = RequestMethod.POST, value = "?op=load")
    public boolean load(@RequestParam(name = "file") MultipartFile file) {
        return appSourceService.load(file);
    }


    public App add(App app) {
        appService.save(app);
        return app;
    }

    public App update(App app) {
        appService.updateById(app);
        return appService.getById(app.getName());
    }





}

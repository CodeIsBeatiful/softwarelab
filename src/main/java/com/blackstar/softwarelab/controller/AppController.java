package com.blackstar.softwarelab.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blackstar.softwarelab.common.BaseController;
import com.blackstar.softwarelab.common.DbConst;
import com.blackstar.softwarelab.entity.App;
import com.blackstar.softwarelab.entity.AppVersion;
import com.blackstar.softwarelab.service.IAppService;
import com.blackstar.softwarelab.service.IAppSourceService;
import com.blackstar.softwarelab.service.IAppVersionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
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

    @Autowired
    private IAppVersionService appVersionService;

    @Autowired
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();


    @RequestMapping(method = RequestMethod.GET, value = "/{name}")
    public App get(@PathVariable String name) {
        return appService.getById(name);
    }


    @RequestMapping(method = RequestMethod.GET)
    public IPage<App> list(@RequestParam int pageNum, @RequestParam int pageSize) {
        Page<App> appPage = new Page<>(pageNum, pageSize);
        QueryWrapper<App> appQueryWrapper = new QueryWrapper<App>().eq(DbConst.COLUMN_STATUS, DbConst.STATUS_NORMAL);
        return appService.page(appPage, appQueryWrapper);
    }

    @RequestMapping(method = RequestMethod.GET, value = "?op=type")
    public IPage<App> listByType(@RequestParam String type, @RequestParam int pageNum, @RequestParam int pageSize) {
        Page<App> appPage = new Page<>(pageNum, pageSize);
        QueryWrapper<App> appQueryWrapper = new QueryWrapper<App>()
                .eq(DbConst.COLUMN_TYPE, type)
                .eq(DbConst.COLUMN_STATUS, DbConst.STATUS_NORMAL);
        return appService.page(appPage, appQueryWrapper);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/{name}/versions/{version}")
    public AppVersion getVersion(@PathVariable String name, @PathVariable String version) {
        return appVersionService.getOne(new QueryWrapper<AppVersion>().setEntity(new AppVersion().setAppName(name).setVersion(version)));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{name}/versions")
    public List<AppVersion> getVersions(@PathVariable String name) {
        return appVersionService.list(new QueryWrapper<AppVersion>().setEntity(new AppVersion().setAppName(name)));
    }

    @RequestMapping(method = RequestMethod.GET, value = "?op=upgrade")
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


    public AppVersion addVersion(AppVersion appVersion) {
        appVersionService.save(appVersion);
        return appVersion;
    }


}

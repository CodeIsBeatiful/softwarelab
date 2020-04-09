package com.blackstar.softwarelab.app.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blackstar.softwarelab.app.entity.App;
import com.blackstar.softwarelab.app.service.IAppService;
import com.blackstar.softwarelab.common.BaseController;
import com.blackstar.softwarelab.common.DbConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.PrintWriter;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author blackstar
 * @since 2020-03-27
 */
@Controller
@RequestMapping("/api/apps")
public class AppController extends BaseController {


    @Autowired
    private IAppService appService;

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public App get(@PathVariable String id) {
        return appService.getById(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public App add(App app) {
        appService.save(app);
        return app;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public App update(App app) {
        appService.updateById(app);
        return appService.getById(app.getId());
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public boolean remove(@PathVariable String id) {
        App app = new App();
        app.setId(id);
        UpdateWrapper<App> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set(DbConst.COLUMN_STATUS, DbConst.STATUS_DELETE);
        return appService.update(app,updateWrapper);
    }

    public boolean delete(@PathVariable String id){
        return appService.removeById(id);
    }


    @RequestMapping(method = RequestMethod.GET)
    public IPage<App> list(@RequestParam int pageNum, @RequestParam int pageSize) {

        Page<App> appPage = new Page<>(pageNum, pageSize);

        QueryWrapper<App> appQueryWrapper = new QueryWrapper<>();
        appQueryWrapper.eq(DbConst.COLUMN_STATUS, DbConst.STATUS_NORMAL);

        return appService.page(appPage, appQueryWrapper);
    }


    @RequestMapping(value = "/logo/{id}")
    public void getImage(@PathVariable("id") String id, HttpServletResponse response) throws Exception{
        //TODO logo不能入主表，直接存储磁盘
        App app = appService.getById(id);
        byte[] logo = app.getLogo();
        if(logo == null){
            return;
        }
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        response.setContentLength(logo.length);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(logo);
        outputStream.flush();
        outputStream.close();
    }


}

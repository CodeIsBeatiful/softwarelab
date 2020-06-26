package com.blackstar.softwarelab.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blackstar.softwarelab.common.BaseController;
import com.blackstar.softwarelab.common.DbConst;
import com.blackstar.softwarelab.entity.App;
import com.blackstar.softwarelab.service.IAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author blackstar
 * @since 2020-03-27
 */
@RestController
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





}

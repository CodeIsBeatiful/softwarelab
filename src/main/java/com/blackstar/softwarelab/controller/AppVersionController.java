package com.blackstar.softwarelab.controller;


import com.blackstar.softwarelab.common.BaseController;
import com.blackstar.softwarelab.entity.AppVersion;
import com.blackstar.softwarelab.service.IAppVersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
@RequestMapping("/api/appVersions")
public class AppVersionController extends BaseController {


    @Autowired
    private IAppVersionService appVersionService;


    @RequestMapping(method = RequestMethod.GET)
    public List<AppVersion> getVersionsByName(HttpServletRequest request, @RequestParam String op) {
        switch (op) {
            case "name":
                String appName = request.getParameter("appName");
                return appVersionService.getVersionsByAppName(appName);
            case "simple":
                appName = request.getParameter("name");
                return appVersionService.getSimpleByAppName(appName);
            default:
                return appVersionService.list();
        }

    }

    @RequestMapping(method = RequestMethod.GET, value = "{name}/{version}")
    public AppVersion getVersionByNameAndVersion(@PathVariable String name,@PathVariable String version) {
        return appVersionService.getVersionByNameAndVersion(name,version);
    }

    public AppVersion addVersion(AppVersion appVersion) {
        appVersionService.save(appVersion);
        return appVersion;
    }

}

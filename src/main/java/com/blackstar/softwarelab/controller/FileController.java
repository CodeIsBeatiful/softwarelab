package com.blackstar.softwarelab.controller;


import com.blackstar.softwarelab.common.BaseController;
import com.blackstar.softwarelab.service.IFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author blackstar
 * @since 2020-06-26
 */
@RestController
@RequestMapping("/api/files")
@Slf4j
public class FileController extends BaseController {

    @Autowired
    private IFileService fileService;

    @RequestMapping(value = "/logo/{app}")
    public void getLogo(@PathVariable("app") String app, HttpServletResponse response) {
        fileService.writeAppLogoToResponse(app, response);
    }
}

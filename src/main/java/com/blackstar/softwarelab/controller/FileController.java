package com.blackstar.softwarelab.controller;


import com.blackstar.softwarelab.entity.File;
import com.blackstar.softwarelab.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import com.blackstar.softwarelab.common.BaseController;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author blackstar
 * @since 2020-06-26
 */
@RestController
@RequestMapping("/api/files")
public class FileController extends BaseController {

    @Autowired
    private IFileService fileService;

    @RequestMapping(value = "/{id}")
    public void getImage(@PathVariable("id") String id, HttpServletResponse response) throws Exception{
        File file = fileService.getById(id);
        if(file == null || file.getData() == null){
            return;
        }
        byte[] image = file.getData();
        if(image == null){
            return;
        }
        //may be MediaType, e.g. MediaType.IMAGE_PNG_VALUE
        response.setContentType(file.getType());
        response.setContentLength(image.length);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(image);
        outputStream.flush();
        outputStream.close();
    }
}

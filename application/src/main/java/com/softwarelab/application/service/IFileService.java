package com.softwarelab.application.service;

import com.softwarelab.application.entity.File;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author blackstar
 * @since 2020-06-26
 */
public interface IFileService extends IService<File> {

    void writeAppLogoToResponse(String app, HttpServletResponse response);
}

package com.blackstar.softwarelab.service;

import com.blackstar.softwarelab.entity.AppSource;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author blackstar
 * @since 2020-07-24
 */
public interface IAppSourceService extends IService<AppSource> {

    boolean upgrade();

    boolean load(MultipartFile file);

    boolean loadToDb();
}

package com.blackstar.softwarelab.service.impl;

import com.blackstar.softwarelab.entity.File;
import com.blackstar.softwarelab.mapper.FileMapper;
import com.blackstar.softwarelab.service.IFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blackstar.softwarelab.util.ResourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author blackstar
 * @since 2020-06-26
 */
@Service
@Slf4j
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements IFileService {

    private static final String SEARCH_PATHS="file:./../sources/logos/,classpath:/source/logos/";

    private static final String LOGO_TYPE=".png";

    @Override
    public void writeAppLogoToResponse(String app, HttpServletResponse response) {
        String fileName = app+LOGO_TYPE;

        InputStream resourceAsStream = ResourceUtil.getResourceAsStream(fileName, SEARCH_PATHS);
        if(resourceAsStream == null){
            return;
        }
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] bytes = new byte[4096];
            int len;
            int fileSize = 0;
            while((len = resourceAsStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                fileSize+=len;

            }
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
            response.setContentLength(fileSize);
            resourceAsStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            log.error("get logo error",e);
        }
    }
}

package com.blackstar.softwarelab.util;


import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileUtil {


    public static String upload(MultipartFile file, String path){
        String fileName = file.getOriginalFilename();
        // 扩展名 abc.jpg
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        // 避免重名
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;

        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.mkdirs();
        }

        File targetFile = new File(path,uploadFileName);
        try {
            file.transferTo(targetFile); // 文件已经上传成功了 // 已经上传到ftp服务器上
        } catch (IOException e) {
            return null;
        }
        // abc.jpg 返回的是最终文件名还是一开始的文件名？
        return targetFile.getName();
    }


}

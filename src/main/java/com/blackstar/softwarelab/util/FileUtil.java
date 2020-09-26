package com.blackstar.softwarelab.util;


import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

public class FileUtil {


    public static String upload(MultipartFile file, String targetPath) {
        String fileName = file.getOriginalFilename();
        // 扩展名 abc.jpg
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 避免重名
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;

        File fileDir = new File(targetPath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        File targetFile = new File(targetPath, uploadFileName);
        try {
            file.transferTo(targetFile); // 文件已经上传成功了 // 已经上传到ftp服务器上
        } catch (IOException e) {
            return null;
        }
        // abc.jpg 返回的是最终文件名还是一开始的文件名？
        return targetFile.getName();
    }


    public static byte[] getContent(String filePath) {
        byte[] bytes = null;
        File file = new File(filePath);
        if (!file.exists()) {
            return bytes;
        }else {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                bytes = new byte[fileInputStream.available()];
                fileInputStream.read(bytes);
                fileInputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }


}

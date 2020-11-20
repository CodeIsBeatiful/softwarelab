package com.softwarelab.application.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author BlackStar
 * @title: ResourceUtil
 * @description:
 * @date 2019/12/3 11:09
 */
@Slf4j
public class ResourceUtil {

    private static final String DEFAULT_SEARCH_PATHS = "file:./config/,file:/config/,classpath:/";


    public static InputStream getResourceAsStream(String fileName, String searchPathStr) {
        String[] searchPaths;
        if (searchPathStr != null) {
            searchPaths = searchPathStr.split(",");
        }else {
            searchPaths = DEFAULT_SEARCH_PATHS.split(",");
        }

        for (int i = 0; i < searchPaths.length; i++) {
            //process placeholders and join
            String searchPath = searchPaths[i];
            if (searchPath.startsWith("classpath:/")) {
                return ResourceUtil.class.getClassLoader().getResourceAsStream(searchPath.substring(11) + fileName);
            } else if (searchPath.startsWith("file:")) {
                searchPath = searchPath.substring(5);
                if (searchPath.startsWith("./")) {
                    String path = ResourceUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile();
                    //fix on linux
                    if (path.startsWith("file:")) {
                        path = path.substring(5);
                    }
                    //fix on spring
                    int separator = path.indexOf("!/");
                    if (separator > 0) {
                        path = path.substring(0, separator);
                    }
                    if (path.endsWith(".jar")) {
                        int lastSeparatorCharIndex = path.lastIndexOf(File.separatorChar);
                        if (lastSeparatorCharIndex > 0) {
                            path = path.substring(0, lastSeparatorCharIndex);
                        }
                    }
                    searchPath = searchPath.replaceFirst("./", path);
                }
                searchPath += fileName;

                try {
                    File file = new File(searchPath);
                    if (file.exists() && file.isFile()) {
                        return new FileInputStream(new File(searchPath));
                    }
                } catch (FileNotFoundException e) {
                    continue;
                }
            }
        }
        return null;
    }

}

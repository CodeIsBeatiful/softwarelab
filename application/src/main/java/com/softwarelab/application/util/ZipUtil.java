package com.softwarelab.application.util;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.Enumeration;

public class ZipUtil {

    static final String pathSeparator = File.separator;

    /**
     *
     * @param zipFilePath zip文件路径
     * @param targetDir 目标目录
     * @param ignoreDepth 忽略文件层数,可以按需从根开始忽略
     */
    public static void unZip(String zipFilePath, String targetDir, int ignoreDepth) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(zipFilePath, "gbk");
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry zipArchiveEntry = entries.nextElement();
                String entryName = zipArchiveEntry.getName();
                String[] paths = entryName.split(pathSeparator);
                if(ignoreDepth > paths.length){
                    throw new RuntimeException("ignore depth error");
                }
                String relativePath;
                StringBuilder relativePathStringBuilder  = new StringBuilder();
                for (int i = 0; i < paths.length-ignoreDepth; i++) {
                    relativePathStringBuilder.append(paths[ignoreDepth+i]+pathSeparator);
                }
                if(relativePathStringBuilder.length()>0){
                    relativePath = relativePathStringBuilder.toString().substring(0,relativePathStringBuilder.length()-1);
                }else{
                    relativePath="";
                }
                String targetPath = targetDir + pathSeparator + relativePath;
                File file = new File(targetPath);
                if (zipArchiveEntry.isDirectory()) {
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                } else {
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                    try (InputStream inputStream = zipFile.getInputStream(zipArchiveEntry);
                         FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                        IOUtils.copy(inputStream,fileOutputStream);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //close zip file
            try {
                zipFile.close();
            } catch (IOException e) {
                //do nothing
            }
        }
    }


    public static void main(String[] args) {

        String targetDir = "/Users/blackstar/opt/software-lab/source";

        String zipFilePath = "/Users/blackstar/github/softwarelab/source.zip";

        int ignoreDepth = 1;

        unZip(zipFilePath,targetDir,ignoreDepth);


    }
}

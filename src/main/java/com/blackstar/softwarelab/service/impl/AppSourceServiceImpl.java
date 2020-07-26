package com.blackstar.softwarelab.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blackstar.softwarelab.bean.AppSourceInfo;
import com.blackstar.softwarelab.bean.SourceRelease;
import com.blackstar.softwarelab.entity.App;
import com.blackstar.softwarelab.entity.AppSource;
import com.blackstar.softwarelab.entity.AppVersion;
import com.blackstar.softwarelab.mapper.AppSourceMapper;
import com.blackstar.softwarelab.service.IAppService;
import com.blackstar.softwarelab.service.IAppSourceService;
import com.blackstar.softwarelab.service.IAppVersionService;
import com.blackstar.softwarelab.util.ZipUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author blackstar
 * @since 2020-07-24
 */
@Service
public class AppSourceServiceImpl extends ServiceImpl<AppSourceMapper, AppSource> implements IAppSourceService {


    private ObjectMapper objectMapper = new ObjectMapper();

    private String targetDir = "source";

    private String appsDir = targetDir + File.separator + "apps";

    private int ignoreDepth = 1;

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    private IAppService appService;

    @Autowired
    private IAppVersionService appVersionService;


    @Override
    public boolean upgrade() {
        AppSource appSource = list().get(0);
        String forObject = restTemplate.getForObject("https://api.github.com/repos/CodeIsBeatiful/softwarelab-source/releases", String.class);
        try {
            CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, SourceRelease.class);
            List<SourceRelease> list = objectMapper.readValue(forObject, collectionType);
            SourceRelease latestSourceRelease = list.get(0);
            if (!latestSourceRelease.getTagName().equals(appSource.getVersion())) {
                ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity(latestSourceRelease.getZipballUrl(), byte[].class);
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    byte[] body = responseEntity.getBody();
                    File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".source");
                    try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
                        fileOutputStream.write(body);
                        fileOutputStream.flush();
                    }
                    ZipUtil.unZip(tempFile.getPath(), targetDir, ignoreDepth);
                    return loadToDb();
                }
            }
        } catch (IOException e) {
            log.error("upgrade apps error", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean load(MultipartFile file) {
        try {
            File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".source");
            file.transferTo(tempFile);
            ZipUtil.unZip(tempFile.getPath(), targetDir, ignoreDepth);

        } catch (IOException e) {
            log.error("load apps error", e);
            return false;
        }
        return loadToDb();
    }

    @Transactional
    public boolean loadToDb() {
        File appsFile = new File(appsDir);
        String[] fileNames = appsFile.list();
        for (String fileName : fileNames) {
            try {
                File appFile = new File(appsDir + File.separator + fileName);
                AppSourceInfo appSourceInfo = objectMapper.readValue(appFile, AppSourceInfo.class);
                String appName = fileName.split("\\.")[0];
                appService.save(App.builder()
                        .name(appName)
                        .type(appSourceInfo.getType())
                        .description(appSourceInfo.getDescription())
                        .additionalInfo(appSourceInfo.getAdditionalInfo().asText())
                        .build());
                List<AppSourceInfo.AppSourceVersion> versions = appSourceInfo.getVersions();
                if (versions != null && versions.size() > 0) {
                    for (AppSourceInfo.AppSourceVersion appSourceVersion : versions) {
                        appVersionService.save(AppVersion.builder()
                                .appName(appName)
                                .version(appSourceVersion.getVersion())
                                .additionalInfo(appSourceVersion.getAdditionalInfo() != null ? appSourceVersion.getAdditionalInfo().asText() : null)
                                .build());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}

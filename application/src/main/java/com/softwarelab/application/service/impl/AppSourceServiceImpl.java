package com.softwarelab.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.softwarelab.application.bean.AppSourceInfo;
import com.softwarelab.application.bean.SourceRelease;
import com.softwarelab.application.common.DbConst;
import com.softwarelab.application.entity.App;
import com.softwarelab.application.entity.AppSource;
import com.softwarelab.application.entity.AppVersion;
import com.softwarelab.application.mapper.AppSourceMapper;
import com.softwarelab.application.service.IAppService;
import com.softwarelab.application.service.IAppSourceService;
import com.softwarelab.application.service.IAppVersionService;
import com.softwarelab.application.util.FileUtil;
import com.softwarelab.application.util.ZipUtil;
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
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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

    private String logosDir = targetDir + File.separator + "logos";

    private String versionFileName = "version";

    private int ignoreDepth = 1;

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    private IAppService appService;

    @Autowired
    private IAppVersionService appVersionService;

    @Autowired
    private IAppSourceService appSourceService;


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
        LocalDateTime now = LocalDateTime.now();
        File appsFile = new File(appsDir);
        String[] fileNames = appsFile.list();
        for (String fileName : fileNames) {
            try {
                File appFile = new File(appsDir + File.separator + fileName);
                AppSourceInfo appSourceInfo = objectMapper.readValue(appFile, AppSourceInfo.class);
                String appName = fileName.split("\\.")[0];
                appService.saveOrUpdate(App.builder()
                        .name(appName)
                        .type(appSourceInfo.getType())
                        .description(appSourceInfo.getDescription())
                        .additionalInfo(appSourceInfo.getAdditionalInfo().toString())
                        .logo(FileUtil.getContent(System.getProperty("data.path") + File.separator + logosDir + File.separator + appName + ".png"))
                        .createTime(now)
                        .updateTime(now)
                        .status(DbConst.STATUS_NORMAL)
                        .build());
                List<AppSourceInfo.AppSourceVersion> versions = appSourceInfo.getVersions();
                if (versions != null && versions.size() > 0) {
                    for (AppSourceInfo.AppSourceVersion appSourceVersion : versions) {
                        QueryWrapper<AppVersion> appVersionWrapper = new QueryWrapper<AppVersion>()
                                .eq(DbConst.COLUMN_APP_NAME, appName)
                                .eq(DbConst.COLUMN_VERSION, appSourceVersion.getVersion());
                        AppVersion existAppVersion = appVersionService.getOne(appVersionWrapper);
                        AppVersion newAppVersion = AppVersion.builder()
                                .appName(appName)
                                .version(appSourceVersion.getVersion())
                                .additionalInfo(appSourceVersion.getAdditionalInfo() != null ? appSourceVersion.getAdditionalInfo().toString() : null)
                                .createTime(now)
                                .updateTime(now)
                                .status(DbConst.STATUS_NORMAL)
                                .downloadStatus(DbConst.DOWNLOAD_STATUS_INIT)
                                .build();
                        if(existAppVersion == null) {
                            appVersionService.save(newAppVersion);
                        } else {
                            appVersionService.update(newAppVersion,appVersionWrapper);
                        }
                    }
                }

            } catch (IOException e) {
                log.error("load to db error",e);
                return false;
            }
        }
        byte[] versionContext = FileUtil.getContent(targetDir + File.separator + versionFileName);
        if (versionContext == null) {
            log.error("version file can't find");
            return false;

        }
        appSourceService.saveOrUpdate(AppSource.builder()
                .id("00000000-0000-0000-0000-000000000000")
                //set true version
                .version(new String(versionContext, StandardCharsets.UTF_8))
                .repository(null)
                .createTime(now)
                .updateTime(now)
                .status(DbConst.STATUS_NORMAL)
                .build());
        return true;
    }
}

package com.softwarelab.application.service;

import com.softwarelab.application.entity.App;
import com.softwarelab.application.entity.AppVersion;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class AppVersionServiceTest extends AbstractServiceBaseTest {


    @Autowired
    private IAppVersionService appVersionService;

    private App demoApp = getDemoApp();


    @Test
    public void getSimpleByAppName() {
        assertTrue(appVersionService.getSimpleByAppName("null").size() == 0);
        List<AppVersion> simpleAppVersions = appVersionService.getSimpleByAppName(demoApp.getName());
        assertTrue(simpleAppVersions.size() == 1);
        AppVersion appVersion = simpleAppVersions.get(0);
        assertTrue(appVersion.getVersion() != null);
        assertTrue(appVersion.getDownloadStatus() != null);

    }

    @Test
    public void getVersionsByAppName() {
        assertTrue(appVersionService.getVersionsByAppName("null").size() == 0);
        List<AppVersion> appVersions = appVersionService.getVersionsByAppName(demoApp.getName());
        assertTrue(appVersions.size() == 1);
        AppVersion appVersion = appVersions.get(0);
        assertTrue(appVersion.getAppName() != null);
        assertTrue(appVersion.getVersion() != null);
    }

    @Test
    public void getVersionByNameAndVersion() {
        AppVersion demoAppVersion = getDemoAppVersion();
        assertTrue(appVersionService.getVersionByNameAndVersion("null","null")  == null);
        AppVersion appVersion = appVersionService.getVersionByNameAndVersion(demoApp.getName(), demoAppVersion.getVersion());
        assertTrue(appVersion != null);
        assertTrue(appVersion.getAppName() != null);
        assertTrue(appVersion.getVersion() != null);
    }
}
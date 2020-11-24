package com.softwarelab.application.service;

import com.softwarelab.application.AbstractBaseTest;
import com.softwarelab.application.entity.App;
import com.softwarelab.application.entity.AppVersion;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

@Transactional
public class AppVersionServiceTest extends AbstractBaseTest {


    @Autowired
    private IAppVersionService appVersionService;

    @Autowired
    private IAppService appService;

    private App demoApp;

    private AppVersion demoAppVersion;


    @Before
    public void setUp() throws Exception {
        demoApp = getDemoApp();
        appService.save(demoApp);
        demoAppVersion = getDemoAppVersion();
        appVersionService.save(demoAppVersion);

    }

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
        assertTrue(appVersionService.getVersionByNameAndVersion("null","null")  == null);
        AppVersion appVersion = appVersionService.getVersionByNameAndVersion(demoApp.getName(), demoAppVersion.getVersion());
        assertTrue(appVersion != null);
        assertTrue(appVersion.getAppName() != null);
        assertTrue(appVersion.getVersion() != null);
    }
}
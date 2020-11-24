package com.softwarelab.application.service;

import com.softwarelab.application.AbstractBaseTest;
import com.softwarelab.application.entity.App;
import com.softwarelab.application.service.IAppExtensionService;
import com.softwarelab.application.service.IAppService;
import com.sun.jna.platform.win32.ShellAPI;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Transactional
public class AppExtensionServiceTest extends AbstractBaseTest {


    @Autowired
    private IAppExtensionService appExtensionService;

    @Autowired
    private IAppService appService;

    private App demoApp;

    @Before
    public void setUp() throws Exception {
        demoApp = getDemoApp();
        appService.save(demoApp);
    }

    @Test
    public void addUsedCount() {
        appExtensionService.addUsedCount(demoApp.getName());
        assertTrue(appExtensionService.getById(demoApp.getName())!=null);
    }
}
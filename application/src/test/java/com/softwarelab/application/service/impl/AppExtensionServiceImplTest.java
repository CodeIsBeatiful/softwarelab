package com.softwarelab.application.service.impl;

import com.softwarelab.application.AbstractBaseTest;
import com.softwarelab.application.entity.App;
import com.softwarelab.application.service.IAppExtensionService;
import com.softwarelab.application.service.IAppService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Transactional
public class AppExtensionServiceImplTest extends AbstractBaseTest {


    @Autowired
    private IAppExtensionService appExtensionService;

    @Autowired
    private IAppService appService;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void addUsedCount() {
        App demoApp = getDemoApp();
        appService.save(demoApp);
        appExtensionService.addUsedCount(demoApp.getName());
        assertTrue(appExtensionService.getById(demoApp.getName())!=null);
    }
}
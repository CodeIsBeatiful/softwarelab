package com.softwarelab.application.service;

import com.softwarelab.application.entity.App;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

public class AppExtensionServiceTest extends AbstractServiceBaseTest {


    @Autowired
    private IAppExtensionService appExtensionService;

    private App demoApp = getDemoApp();

    @Test
    public void addUsedCount() {
        appExtensionService.addUsedCount(demoApp.getName());
        assertTrue(appExtensionService.getById(demoApp.getName())!=null);
    }
}
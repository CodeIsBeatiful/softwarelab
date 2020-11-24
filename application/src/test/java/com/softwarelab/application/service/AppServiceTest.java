package com.softwarelab.application.service;

import com.softwarelab.application.AbstractBaseTest;
import com.softwarelab.application.bean.AppInfo;
import com.softwarelab.application.entity.App;
import com.softwarelab.application.service.IAppService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@Transactional
public class AppServiceTest extends AbstractBaseTest {

    @Autowired
    private IAppService appService;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void getNameByType() {
        App demoApp = getDemoApp();
        appService.save(demoApp);
       assertTrue(appService.getNameByType("null").size() == 0);
       assertTrue(appService.getNameByType(demoApp.getType()).size() == 1);
    }

    @Test
    public void getByName() {
        App demoApp = getDemoApp();
        appService.save(demoApp);
        assertTrue(appService.getByName("null") == null);
        assertTrue(appService.getByName(demoApp.getName()) != null);
    }

    @Test
    public void getTop() {
        App demoApp = getDemoApp();
        appService.save(demoApp);
        assertTrue(appService.getTop(0).size() >= 0);
        assertTrue(appService.getTop(1).size() >= 1);
        demoApp.setName("demo2");
        appService.save(demoApp);
        assertTrue(appService.getTop(2).size() >= 2);

    }
}
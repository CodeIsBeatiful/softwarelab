package com.softwarelab.application.service;

import com.softwarelab.application.entity.App;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

public class AppServiceTest extends AbstractServiceBaseTest {

    @Autowired
    private IAppService appService;

    private App demoApp  = getDemoApp();


    @Test
    public void getNameByType() {
       assertTrue(appService.getNameByType("null").size() == 0);
       assertTrue(appService.getNameByType(TEST_APP_TYPE).size() == 2);
    }

    @Test
    public void getByName() {
        assertTrue(appService.getByName("null") == null);
        assertTrue(appService.getByName(demoApp.getName()) != null);
    }

    @Test
    public void getTop() {
        assertTrue(appService.getTop(0).size() >= 0);
        assertTrue(appService.getTop(1).size() >= 1);
        demoApp.setName("demo2");
        appService.save(demoApp);
        assertTrue(appService.getTop(2).size() >= 2);

    }
}
package com.softwarelab.application.controller;

import com.softwarelab.application.AbstractBaseTest;
import com.softwarelab.application.entity.App;
import com.softwarelab.application.entity.AppVersion;
import com.softwarelab.application.entity.Instance;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;


@Transactional
public class InstanceControllerTest extends AbstractBaseTest {


    @Autowired
    private InstanceController instanceController;

    @Autowired
    private AppController appController;

    @Autowired
    private AppVersionController appVersionController;

    private Instance instance;
    private App app;
    private AppVersion appVersion;

    private final static String[] canSortStr = {"updateTime", "createTime", "runningStatus", "name"};

    @Before
    public void setUp() throws Exception {
        app = getDemoApp();
        appVersion = getDemoAppVersion();
        instance = getDemoInstance();
    }

    @Test
    public void testCRUD() {
        appController.add(app);
        appVersionController.addVersion(appVersion);
        instance = getDemoInstance();
        //instance ---> container
        //save instance
        //todo how to get security user
        instance = instanceController.add(instance);
        Instance savedInstance = instanceController.get(this.instance.getId());
        assertNotNull(savedInstance);
        assertNotNull(savedInstance.getId());
        assertNotNull(savedInstance.getName());

        //delete instance and remove container
        instanceController.delete(this.instance.getId());
        Instance deleteForeverInstance = instanceController.get(this.instance.getId());
        assertNull(deleteForeverInstance);


    }


    @Test
    public void list() {
        appController.add(app);
        appVersionController.addVersion(appVersion);
        instanceController.add(instance);
        int pageNum = 0;
        int pageSize = 5;
        instanceController.list(pageNum, pageSize, AbstractBaseTest.DEMO_NAME, null, null);
        //todo
    }

}
package com.softwarelab.application.controller;

import com.softwarelab.application.AbstractBaseTest;
import com.softwarelab.application.bean.ContainerInfo;
import com.softwarelab.application.bean.ContainerPortSetting;
import com.softwarelab.application.common.DbConst;
import com.softwarelab.application.entity.App;
import com.softwarelab.application.entity.AppVersion;
import com.softwarelab.application.entity.Instance;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;


@Transactional
public class InstanceControllerTest extends AbstractBaseTest {


    @Autowired
    private InstanceController instanceController;



    private Instance instance;

    private final static String[] canSortStr = {"updateTime", "createTime", "runningStatus", "name"};

    @Before
    public void setUp() throws Exception {
        instance =getDemoInstance();

    }

    @Test
    public void testCRUD() {
        //instance ---> container
        //save instance
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
    public void list(){
        instanceController.add(instance);
        int pageNum = 0;
        int pageSize = 5;
        instanceController.list(pageNum,pageSize,AbstractBaseTest.DEMO_NAME,null,null);
        //todo
    }

}
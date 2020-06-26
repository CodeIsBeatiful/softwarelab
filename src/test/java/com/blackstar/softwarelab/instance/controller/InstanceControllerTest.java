package com.blackstar.softwarelab.instance.controller;

import com.blackstar.softwarelab.AbstractBaseTest;
import com.blackstar.softwarelab.instance.entity.Instance;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


@Transactional
public class InstanceControllerTest extends AbstractBaseTest {



    private Instance instance;


    @Before
    public void setUp() throws Exception {

        instance = new Instance();
        instance.setId(UUID.randomUUID().toString());
        instance.setUserId(UUID.randomUUID().toString());
        instance.setAppId(UUID.randomUUID().toString());
        instance.setName("test_metabase");
        LocalDateTime now = LocalDateTime.now();
        instance.setCreateTime(now);
        instance.setUpdateTime(now);
        instance.setStatus(0);
    }

    @Test
    public void testSave(){

    }
}
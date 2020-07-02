package com.blackstar.softwarelab.controller;

import com.blackstar.softwarelab.AbstractBaseTest;
import com.blackstar.softwarelab.bean.ContainerInfo;
import com.blackstar.softwarelab.entity.Instance;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.*;


@Transactional
public class InstanceControllerTest extends AbstractBaseTest {


    @Autowired
    private InstanceController instanceController;



    private Instance instance;


    @Before
    public void setUp() throws Exception {

        instance = new Instance();
        instance.setUserId(UUID.randomUUID().toString());
        instance.setName("my_metabase_v0.35.4");
        instance.setAppName("metabase");
        instance.setAppVersion("v0.35.4");
        LocalDateTime now = LocalDateTime.now();
        instance.setCreateTime(now);
        instance.setUpdateTime(now);
        instance.setStatus(0);

        ContainerInfo containerInfo = ContainerInfo.builder()
                .imageName("metabase/metabase")
                .ports(Arrays.asList("31000:3000"))
                .build();
        instance.setAdditionalInfo(new ObjectMapper().writeValueAsString(containerInfo));
    }

    @Test
    public void testCRUD(){
        instance = instanceController.add(instance);
        Instance savedInstance = instanceController.get(this.instance.getId());
        assertNotNull(savedInstance);
        assertNotNull(savedInstance.getId());

        instanceController.delete(this.instance.getId());
        Instance deleteForeverInstance = instanceController.get(this.instance.getId());
        assertNull(deleteForeverInstance);

    }
}
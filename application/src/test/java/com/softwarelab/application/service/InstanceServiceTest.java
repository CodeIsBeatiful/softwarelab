package com.softwarelab.application.service;

import com.softwarelab.application.common.DbConst;
import com.softwarelab.application.entity.Instance;
import com.softwarelab.application.entity.SysUser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class InstanceServiceTest extends AbstractServiceBaseTest {


    @Autowired
    private IInstanceService instanceService;

    @Test
    public void testCRUD(){
        //add
        SysUser testUser = getTestUser();
        Instance demoInstance = getDemoInstance();
        //new uuid
        demoInstance.setId(UUID.randomUUID().toString());
        instanceService.add(testUser.getId(),demoInstance);
        demoInstance = instanceService.getById(demoInstance.getId());
        assertNotNull(demoInstance);
        try {
            //start
            instanceService.start(testUser.getId(),demoInstance.getId());
            TimeUnit.SECONDS.sleep(3);
            demoInstance = instanceService.getById(demoInstance.getId());
            assertEquals(demoInstance.getRunningStatus().intValue(),DbConst.RUNNING_STATUS_START);
            //stop
            instanceService.stop(testUser.getId(),demoInstance.getId());
            TimeUnit.SECONDS.sleep(3);
            demoInstance = instanceService.getById(demoInstance.getId());
            assertEquals(demoInstance.getRunningStatus().intValue(),DbConst.RUNNING_STATUS_STOP);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //delete
        instanceService.delete(demoInstance.getId());
        assertNull(instanceService.getById(demoInstance.getId()));

    }

}
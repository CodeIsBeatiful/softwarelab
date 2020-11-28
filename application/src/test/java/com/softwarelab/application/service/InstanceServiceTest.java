package com.softwarelab.application.service;

import com.softwarelab.application.AbstractBaseTest;
import com.softwarelab.application.common.DbConst;
import com.softwarelab.application.entity.Instance;
import com.softwarelab.application.entity.SysUser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@Transactional
public class InstanceServiceTest extends AbstractBaseTest {


    @Autowired
    private IInstanceService instanceService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private IAppService appService;

    @Autowired
    private IAppVersionService appVersionService;

    private SysUser demoUser;

    @Before
    public void setUp() throws Exception {
        checkTestImage();
        demoUser = getDemoUser();
        sysUserService.save(demoUser);
        appService.save(this.getDemoApp());
        appVersionService.save(this.getDemoAppVersion());


    }

    @Test
    public void testCRUD(){
        //add
        Instance demoInstance = getDemoInstance();
        instanceService.add(demoUser.getId(),demoInstance);
        demoInstance = instanceService.getById(demoInstance.getId());
        assertNotNull(demoInstance);
        try {
            //start
            instanceService.start(demoUser.getId(),demoInstance.getId());
            TimeUnit.SECONDS.sleep(3);
            demoInstance = instanceService.getById(demoInstance.getId());
            assertEquals(demoInstance.getRunningStatus().intValue(),DbConst.RUNNING_STATUS_START);
            //stop
            instanceService.stop(demoUser.getId(),demoInstance.getId());
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
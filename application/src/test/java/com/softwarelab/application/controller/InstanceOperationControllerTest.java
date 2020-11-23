package com.softwarelab.application.controller;

import com.softwarelab.application.AbstractBaseTest;
import com.softwarelab.application.common.DbConst;
import com.softwarelab.application.entity.Instance;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Transactional
public class InstanceOperationControllerTest extends AbstractBaseTest {


    @Autowired
    private InstanceController instanceController;



    @Autowired
    private InstanceOperationController operationController;

    private Instance instance;

    @Before
    public void before(){
        instance = getDemoInstance();
        instanceController.add(instance);
    }

    @Test
    public void operate() {
        //start container
        assertTrue(operationController.operate(this.instance.getId(),"start"));
        Instance startingInstance = instanceController.get(this.instance.getId());
        assertTrue(startingInstance.getRunningStatus() == DbConst.RUNNING_STATUS_START);
        //stop container
        assertTrue(operationController.operate(this.instance.getId(),"stop"));
        Instance stoppedInstance = instanceController.get(this.instance.getId());
        assertTrue(stoppedInstance.getRunningStatus() == DbConst.RUNNING_STATUS_STOP);
    }
}
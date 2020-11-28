package com.softwarelab.application.service;

import com.softwarelab.application.AbstractBaseTest;
import com.softwarelab.application.service.IAppSourceService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Transactional
public class AppSourceServiceTest extends AbstractBaseTest {


    @Value("${test.useNetwork")
    private boolean useNetwork;

    @Autowired
    private IAppSourceService appSourceService;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void upgrade() {
        if(useNetwork){
            //todo
        }

    }

    @Test
    public void load() {
        //todo
    }

    @Test
    public void loadToDb() {
        //todo
    }
}
package com.softwarelab.application.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

public class AppSourceServiceTest extends AbstractServiceBaseTest {


    @Value("${test.useNetwork}")
    private boolean useNetwork;

    @Autowired
    private IAppSourceService appSourceService;



    @Test
    public void upgrade() {
        if(!useNetwork){
            return;
        }
        //todo

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
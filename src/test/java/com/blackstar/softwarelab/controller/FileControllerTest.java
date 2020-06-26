package com.blackstar.softwarelab.controller;

import com.blackstar.softwarelab.AbstractBaseTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;


@Transactional
public class FileControllerTest extends AbstractBaseTest {


    @Autowired
    private FileController fileController;


    private File file;

    @Before
    public void setUp() throws Exception {



    }


    @Test
    public void testSave() {

    }



    @Test
    public void testGetImage(){

    }




}
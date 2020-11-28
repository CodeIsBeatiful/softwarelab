package com.softwarelab.application.service;

import com.softwarelab.application.AbstractBaseTest;
import com.softwarelab.application.exception.PortException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class PortServiceTest extends AbstractBaseTest {


    @Autowired
    private IPortService portService;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testPort() {
        try {
            //get random port
            int randomPort = portService.getRandomPort();

            assertFalse(portService.usePort(randomPort));
            portService.releasePort(randomPort);
            //use target port
            assertTrue(portService.usePort(randomPort));
            portService.releasePort(randomPort);
        } catch (PortException e) {
            e.printStackTrace();
        }
    }
}
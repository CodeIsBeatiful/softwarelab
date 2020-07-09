package com.blackstar.softwarelab.util;

import org.junit.Test;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;

import static org.junit.Assert.*;

public class PortUtilTest {

    @Test
    public void canUse() {
        int port = 18081;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            assertFalse(PortUtil.canUse(port));
            serverSocket.close();
            assertTrue(PortUtil.canUse(port));
        } catch (Exception e) {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }
}
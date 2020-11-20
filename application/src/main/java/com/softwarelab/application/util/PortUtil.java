package com.softwarelab.application.util;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author blackstar
 */
public class PortUtil {

    /**
     * test port is not bind,if bind, return false
     * @param port
     * @return
     */
    public static boolean canUse(int port){
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            return true;
        } catch (IOException e) {
            //do nothing
        } finally {
            if ( serverSocket != null ){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}

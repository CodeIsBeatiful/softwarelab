package com.blackstar.softwarelab.service;


import com.blackstar.softwarelab.exception.PortException;

public interface IPortService {

    int getRandomPort() throws PortException;

    boolean usePort(int port) ;

    void releasePort(int port);

}

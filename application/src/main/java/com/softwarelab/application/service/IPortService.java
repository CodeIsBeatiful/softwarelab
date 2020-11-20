package com.softwarelab.application.service;


import com.softwarelab.application.exception.PortException;

public interface IPortService {

    int getRandomPort() throws PortException;

    boolean usePort(int port) ;

    void releasePort(int port);

}

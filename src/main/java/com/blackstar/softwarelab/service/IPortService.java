package com.blackstar.softwarelab.service;


import com.blackstar.softwarelab.exception.PortException;

public interface IPortService {

    public int getRandomPort() throws PortException;

    public boolean usePort(int port) ;

}

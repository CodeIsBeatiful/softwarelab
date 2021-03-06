package com.softwarelab.application.service.impl;

import com.softwarelab.application.bean.ContainerInfo;
import com.softwarelab.application.bean.ContainerPortSetting;
import com.softwarelab.application.exception.PortException;
import com.softwarelab.application.service.IPortService;
import com.softwarelab.application.entity.Instance;
import com.softwarelab.application.service.IInstanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;


@Slf4j
@Service
public class PortServiceImpl implements IPortService {


    @Value("${softwarelab.instance.ports}")
    private String ports;

    @Autowired
    private IInstanceService instanceService;

    private int portCounter = 0;

    private double canUsePortPrecentThreshold = 0.2;

    private double canUserPortCountThreshold = 10;

    private ObjectMapper objectMapper = new ObjectMapper();

    private TreeSet<Integer> canUsePorts = new TreeSet<>();

    private Object lock = new Object();



    @PostConstruct
    public void init() {
        scanPorts();
    }

    private void scanPorts() {
        String[] portPeriod = ports.split("-");
        int startPort = Integer.parseInt(portPeriod[0]);
        int endPort = Integer.parseInt(portPeriod[1]);
        List<Instance> instances = instanceService.list();
        HashSet<Integer> usedPorts = new HashSet<>();
        instances.forEach(instance -> {
            try {
                ContainerInfo containerInfo = objectMapper.readValue(instance.getAdditionalInfo(), ContainerInfo.class);
                List<ContainerPortSetting> ports = containerInfo.getPorts();
                ports.forEach(containerPortSetting -> {
                    //if auto generate port
                    if(containerPortSetting.getTargetPort() == null){
                        return;
                    }
                    usedPorts.add(containerPortSetting.getTargetPort());
                });
            } catch (IOException e) {
                // do nothing
            }
        });
        //scan can use ports
        for (int port = startPort; port <= endPort; port++) {
            if (!usedPorts.contains(port)) {
                canUsePorts.add(port);
            }
        }
        portCounter = canUsePorts.size();

    }

    private void checkPortUsageRate() {
        if (canUsePorts.size() < portCounter * canUsePortPrecentThreshold) {
            scanPorts();
        }
        if(canUsePorts.size() < canUserPortCountThreshold){
            log.warn("Available port less than {} , the quantity is {}",canUserPortCountThreshold,canUsePorts.size());
        }
    }

    public int getRandomPort() throws PortException {
        synchronized (lock){
            Integer port = canUsePorts.pollFirst();
            if(port == null){
                throw new PortException("No available port");
            }
            checkPortUsageRate();
            return port;
        }

    }
    public  boolean usePort(int port) {
        synchronized (lock){
            if(canUsePorts.contains(port)){
                canUsePorts.remove(port);
                checkPortUsageRate();
                return true;
            }
            return false;
        }

    }

    @Override
    public void releasePort(int port) {
        synchronized (lock){
            canUsePorts.add(port);
        }
    }
}
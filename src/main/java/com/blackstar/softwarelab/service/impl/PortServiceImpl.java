package com.blackstar.softwarelab.service.impl;

import com.blackstar.softwarelab.bean.ContainerInfo;
import com.blackstar.softwarelab.entity.Instance;
import com.blackstar.softwarelab.exception.PortException;
import com.blackstar.softwarelab.service.IInstanceService;
import com.blackstar.softwarelab.service.IPortService;
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


    @Value("${instance.ports}")
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
                List<String> ports = containerInfo.getPorts();
                ports.forEach(str -> {
                    String portStr = str.split(":")[0];
                    //if auto generate port
                    if(portStr.length() == 0){
                        return;
                    }
                    usedPorts.add(Integer.parseInt(str.split(":")[0]));
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
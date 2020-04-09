package com.blackstar.softwarelab.instance.service;


import com.blackstar.softwarelab.common.KeyValuePair;
import com.blackstar.softwarelab.instance.bean.ContainerInfo;

import java.util.List;

public interface ContainerService {

    ContainerInfo start(ContainerInfo containerInfo);


    void stop(ContainerInfo containerInfo);


    void remove(ContainerInfo containerInfo);


    List<ContainerInfo> listByIds(List<String> ids);

    List<ContainerInfo> listByLabels(List<KeyValuePair> labels);

    String checkStatus(ContainerInfo containerInfo);
}

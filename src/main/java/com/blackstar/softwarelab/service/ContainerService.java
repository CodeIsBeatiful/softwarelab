package com.blackstar.softwarelab.service;


import com.blackstar.softwarelab.common.KeyValuePair;
import com.blackstar.softwarelab.bean.ContainerInfo;

import java.util.List;
import java.util.Map;

public interface ContainerService {

    ContainerInfo start(ContainerInfo containerInfo);

    void stop(ContainerInfo containerInfo);

    void remove(ContainerInfo containerInfo);

    void removeById(String id);

    List<ContainerInfo> listByIds(List<String> ids);

    List<ContainerInfo> listByNames(List<String> names);

    List<ContainerInfo> listByLabels(List<KeyValuePair> labels);

    String getState(String name);
}

package com.blackstar.softwarelab.service;


import com.blackstar.softwarelab.bean.ContainerInfo;
import com.blackstar.softwarelab.common.KeyValuePair;
import com.blackstar.softwarelab.exception.PortException;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.PullResponseItem;

import java.util.List;

public interface ContainerService {

    ContainerInfo start(ContainerInfo containerInfo) throws PortException;

    void stop(ContainerInfo containerInfo);

    void remove(ContainerInfo containerInfo);

    void removeById(String id);

    List<ContainerInfo> listByIds(List<String> ids);

    List<ContainerInfo> listByNames(List<String> names);

    List<ContainerInfo> listByLabels(List<KeyValuePair> labels);

    String getState(String name);

    boolean hasImage(String imageName);

    ResultCallback.Adapter<PullResponseItem> pullImage(String imageName);

    void removeImage(String imageName);
}

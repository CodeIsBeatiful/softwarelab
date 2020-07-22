package com.blackstar.softwarelab.service;


import com.blackstar.softwarelab.bean.ContainerInfo;
import com.blackstar.softwarelab.common.KeyValuePair;
import com.blackstar.softwarelab.exception.PortException;
import com.blackstar.softwarelab.service.impl.DockerContainerServiceImpl;
import com.github.dockerjava.core.command.ExecStartResultCallback;

import java.io.OutputStream;
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

    DockerContainerServiceImpl.PullImageCallback pullImage(String imageName);

    void removeImage(String imageName);

    ExecStartResultCallback runCommand(String containerId, String command, OutputStream outputStream);
}

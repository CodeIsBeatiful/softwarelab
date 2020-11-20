package com.softwarelab.application.service;


import com.softwarelab.application.bean.ContainerInfo;
import com.softwarelab.application.exception.PortException;
import com.softwarelab.application.service.impl.DockerContainerServiceImpl;
import com.softwarelab.application.common.KeyValuePair;
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

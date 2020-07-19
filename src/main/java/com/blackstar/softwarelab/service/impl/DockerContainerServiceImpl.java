package com.blackstar.softwarelab.service.impl;


import com.blackstar.softwarelab.bean.ContainerInfo;
import com.blackstar.softwarelab.common.ContainerStatusConst;
import com.blackstar.softwarelab.common.KeyValuePair;
import com.blackstar.softwarelab.exception.PortException;
import com.blackstar.softwarelab.service.ContainerService;
import com.blackstar.softwarelab.service.IPortService;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectExecResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

@Slf4j
@Service
public class DockerContainerServiceImpl implements ContainerService {

    private DockerClient dockerClient;

    @Autowired
    private IPortService portService;


    public DockerContainerServiceImpl() {
        initClient();
    }

    private void initClient() {
        DefaultDockerClientConfig config = DefaultDockerClientConfig
                .createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock")
//                .withRegistryUrl("https://index.docker.io/v1/")
                .build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();

        dockerClient = DockerClientImpl.getInstance(config, httpClient);
//        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
//                .dockerHost(config.getDockerHost())
//                .sslConfig(config.getSSLConfig())
//                .build();

//        DockerCmdExecFactory dockerCmdExecFactory = new JerseyDockerCmdExecFactory()
//                .withReadTimeout(1000)
//                .withConnectTimeout(1000);

//        dockerClient = DockerClientBuilder.getInstance(config)
//                .build();
    }



    public ContainerInfo start(ContainerInfo containerInfo) throws PortException {
        //get exist container
        Container container = getContainer(containerInfo);
        if (container == null) {
            String imageName = containerInfo.getImageName();
            Map<String,String> labelMap = getLabelMap(containerInfo.getLabels());
            List<PortBinding> portBindings = getPortBinds(containerInfo.getPorts());
            CreateContainerResponse createContainer = dockerClient.createContainerCmd(imageName)
                    .withName(containerInfo.getName())
                    .withLabels(labelMap)
                    .withPortBindings(portBindings)
                    .withEnv(containerInfo.getEnvs())
//                    .withHostConfig(hostConfig)
                    .exec();

            dockerClient.startContainerCmd(createContainer.getId()).exec();
            containerInfo.setId(createContainer.getId());

        } else {
            if (container.getState().equals(ContainerStatusConst.EXITED) || container.getState().equals(ContainerStatusConst.CREATED)) {
                dockerClient.startContainerCmd(container.getId()).exec();
            } else {
                log.warn("start container  [{}] catch error [{}] ", container.getId(), container.getState());
            }
            containerInfo.setId(container.getId());
        }
        return containerInfo;
    }

    private Map<String, String> getLabelMap(List<String> labels) {
        Map<String, String> labelMap = new HashMap<>();
        labels.forEach(str -> {
            String[] split = str.split(":");
            labelMap.put(split[0], split[1]);
        });
        return labelMap;
    }

    private List<PortBinding> getPortBinds(List<String> ports) throws PortException{
        List<PortBinding> portBindings = new ArrayList<>();
        for (String port : ports) {
            String[] portMap = port.split(":");
            String bindPort = portMap[0];
            int exposePort = Integer.parseInt(portMap[1]);
            //e.g. :8080
            if (bindPort.length() == 0) {
                bindPort = portService.getRandomPort() + "";
            }
            portBindings.add(new PortBinding(new Ports.Binding(null,bindPort),new ExposedPort(exposePort)));
        }
        return portBindings;
    }

    public void stop(ContainerInfo containerInfo) {
        if (containerInfo.getId() == null) {
            return;
        }
        dockerClient.stopContainerCmd(containerInfo.getId()).withTimeout(2000).exec();

    }


    public Container getContainer(ContainerInfo containerInfo) {
        //get containers if exist
        List<Container> containers = dockerClient.listContainersCmd()
                .withIdFilter(Arrays.asList(containerInfo.getId()))
                .withShowAll(true)
                .exec();
        if (containers == null || containers.size() == 0) {
            return null;
        }
        return containers.get(0);
    }

    @Override
    public void remove(ContainerInfo containerInfo) {
        removeById(containerInfo.getId());
    }

    @Override
    public void removeById(String id) {
        if (id == null) {
            throw new RuntimeException("must have container id");
        }
        //force remove
        dockerClient.removeContainerCmd(id).withForce(true).exec();
    }

    @Override
    public List<ContainerInfo> listByIds(List<String> ids) {
        List<Container> containers = dockerClient.listContainersCmd()
                .withIdFilter(ids)
                .withShowAll(true)
                .exec();
        return getContainerInfos(containers);
    }

    @Override
    public List<ContainerInfo> listByNames(List<String> names) {
        List<Container> containers = dockerClient.listContainersCmd()
                .withNameFilter(names)
                .withShowAll(true)
                .exec();
        return getContainerInfos(containers);
    }

    @Override
    public List<ContainerInfo> listByLabels(List<KeyValuePair> labels) {
        Map<String, String> labelMap = new HashMap<>();
        labels.forEach(keyValuePair -> {
            labelMap.put(keyValuePair.getKey(), keyValuePair.getValue());
        });
        List<Container> containers = dockerClient.listContainersCmd()
                .withLabelFilter(labelMap)
                .withShowAll(true)
                .exec();
        return getContainerInfos(containers);
    }


    private List<ContainerInfo> getContainerInfos(List<Container> containers) {
        if (containers == null || containers.size() == 0) {
            return null;
        } else {
            ArrayList<ContainerInfo> containerInfos = new ArrayList<>();
            for (Container container : containers) {
                containerInfos.add(ContainerInfo.builder()
                        // fix bug : name need start with /
                        .name(container.getNames()[0].substring(1))
                        .id(container.getId())
                        .imageName(container.getImage())
                        .status(container.getState())
                        .build());
            }
            return containerInfos;
        }
    }

    @Override
    public String getState(String name) {
        List<Container> containers = dockerClient.listContainersCmd()
                .withNameFilter(Arrays.asList(name))
                .withShowAll(true)
                .exec();
        if (containers == null || containers.size() == 0) {
            return null;
        }
        return containers.get(0).getState();
    }

    @Override
    public boolean hasImage(String imageName) {
        List<Image> images = dockerClient.listImagesCmd().withImageNameFilter(imageName).exec();
        if(images == null || images.size() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public PullImageCallback pullImage(String imageName) {
        return dockerClient.pullImageCmd(imageName).exec(new PullImageCallback());
    }

    @Override
    public void removeImage(String imageName) {
        dockerClient.removeImageCmd(imageName).exec();
    }

    public ExecStartResultCallback runCommand(String containerId,String command,OutputStream outputStream){

        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withCmd(command.split(" "))
                .withUser("root")
                .exec();
        return dockerClient.execStartCmd(execCreateCmdResponse.getId())
                .exec(new ExecStartResultCallback(outputStream, outputStream));

    }

    public static class AttachContainerCallback extends ResultCallback.Adapter<Frame> {
        private StringBuffer log = new StringBuffer();

        @Override
        public void onNext(Frame item) {
            log.append(new String(item.getPayload()));
            super.onNext(item);
        }

        @Override
        public String toString() {
            return log.toString();
        }
    }

    public static class PullImageCallback extends ResultCallback.Adapter<PullResponseItem> {

    }

}

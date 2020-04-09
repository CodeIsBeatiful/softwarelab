package com.blackstar.softwarelab.instance.service.impl;


import com.blackstar.softwarelab.common.KeyValuePair;
import com.blackstar.softwarelab.instance.bean.ContainerInfo;
import com.blackstar.softwarelab.instance.service.ContainerService;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class DockerServiceImpl implements ContainerService {


    private DockerClient dockerClient;


    public DockerServiceImpl() {
        initClient();
    }

    private void initClient() {
        DefaultDockerClientConfig config = DefaultDockerClientConfig
                .createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock")
                .build();

        DockerCmdExecFactory dockerCmdExecFactory = new JerseyDockerCmdExecFactory()
                .withReadTimeout(1000)
                .withConnectTimeout(1000);

        dockerClient = DockerClientBuilder.getInstance(config)
                .withDockerCmdExecFactory(dockerCmdExecFactory)
                .build();
    }


    public ContainerInfo start(ContainerInfo containerInfo) {
        //get exist container
        Container container = getContainer(containerInfo);
        if (container == null) {
            String imageName = containerInfo.getImageName();
            List<String> ports = containerInfo.getPorts();
            List<PortBinding> portBindings = new ArrayList<>();
            List<String> labels = containerInfo.getLabels();
            Map<String, String> labelMap = new HashMap<>();
            labels.forEach(str -> {
                String[] split = str.split(":");
                labelMap.put(split[0],split[1]);
            });
            ports.forEach(str -> {
                portBindings.add(PortBinding.parse(str));
            });
            CreateContainerResponse createContainer = dockerClient.createContainerCmd(imageName)
                    .withName(containerInfo.getName())
                    .withLabels(labelMap)
                    .withPortBindings(portBindings)
//                    .withHostConfig(hostConfig)
                    .exec();

            dockerClient.startContainerCmd(createContainer.getId()).exec();
            containerInfo.setId(createContainer.getId());

        } else {
            if (container.getStatus().startsWith("Exited")) {
                dockerClient.startContainerCmd(container.getId()).exec();
            } else {
                log.warn(container.getNames()[0] + "is started before");
            }
            containerInfo.setId(container.getId());
        }
        return containerInfo;
    }


    public void stop(ContainerInfo containerInfo) {
        if (containerInfo.getId() == null) {
            return;
        }
        dockerClient.stopContainerCmd(containerInfo.getId()).withTimeout(2000).exec();

    }


    public Container getContainer(ContainerInfo containerInfo){
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
        if (containerInfo.getId() == null) {
            throw new RuntimeException("must have container id");
        }
        //force remove
        dockerClient.removeContainerCmd(containerInfo.getId()).withForce(true).exec();
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
    public List<ContainerInfo> listByLabels(List<KeyValuePair> labels){
        Map<String, String> labelMap = new HashMap<>();
        labels.forEach(keyValuePair -> {
            labelMap.put(keyValuePair.getKey(),keyValuePair.getValue());
        });
        List<Container> containers = dockerClient.listContainersCmd()
                .withLabelFilter(labelMap)
                .withShowAll(true)
                .exec();
        return getContainerInfos(containers);
    }


    @Override
    public String checkStatus(ContainerInfo containerInfo) {
        List<ContainerInfo> containerInfos = listByIds(Arrays.asList(containerInfo.getId()));
        //DOCKER STATUS
        if(containerInfos == null || containerInfos.size() == 0){
            return null;
        }
        return containerInfos.get(0).getStatus().split("_")[0];
    }

    private List<ContainerInfo> getContainerInfos(List<Container> containers) {
        if (containers == null || containers.size() == 0) {
            return null;
        } else {
            ArrayList<ContainerInfo> containerInfos = new ArrayList<>();
            for (Container container : containers) {
                containerInfos.add(ContainerInfo.builder()
                        .name(container.getNames()[0])
                        .id(container.getId())
                        .imageName(container.getImage())
                        .status(container.getStatus())
                        .build());
            }
            return containerInfos;
        }
    }


}

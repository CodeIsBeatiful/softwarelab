package com.softwarelab.application.service;

import com.softwarelab.application.AbstractBaseTest;
import com.softwarelab.application.bean.ContainerEnvSetting;
import com.softwarelab.application.bean.ContainerInfo;
import com.softwarelab.application.bean.ContainerPortSetting;
import com.softwarelab.application.exception.PortException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.softwarelab.application.service.ContainerService;
import com.softwarelab.application.service.impl.DockerContainerServiceImpl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;


public class DockerContainerServiceTest extends AbstractBaseTest {


    @Autowired
    private ContainerService containerService;

    private ContainerInfo containerInfo;

    private int port;

    @Before
    public void setUp() throws Exception {
        checkTestImage();
        port = AbstractBaseTest.DEMO_TARGET_PORT;
        List<ContainerPortSetting> ports = new ArrayList<>();
        //machine 5444, container inner port 5432
        ports.add(ContainerPortSetting.builder().targetPort(port).port(8080).build());
        List<String> labels = new ArrayList<>();
        labels.add("test:demo");
        List<ContainerEnvSetting> envs = new ArrayList<>();
        envs.add(ContainerEnvSetting.builder()
//                .key("POSTGRES_PASSWORD").value("postgres")
                .build());
        containerInfo = ContainerInfo.builder()
                .imageName(AbstractBaseTest.DEMO_IMAGE_TAG)
                .name(AbstractBaseTest.DEMO_IMAGE_TAG.replace('/', '_').replace(':', '_'))
                .ports(ports)
                .labels(labels)
                .envs(envs)
                .build();
    }


    @Test
    public void testStartAndStop() {
        try {
            containerService.start(containerInfo);
        } catch (PortException e) {
            e.printStackTrace();
        }
        //get container
        Container container = containerService.getContainer(containerInfo);
        assertNotNull(container);
        try {
            TimeUnit.SECONDS.sleep(5);
            //get Info from Url
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://localhost:" + port)
                    .build();
            Response response = client.newCall(request).execute();
            assertEquals("hello,softwarelab", response.body().string());
            ;

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            containerService.stop(containerInfo);
            containerService.remove(containerInfo);
        }


    }


    @Test
    public void testImage() {
        assertTrue(containerService.hasImage(AbstractBaseTest.DEMO_IMAGE_TAG));
    }

    @Test
    public void testPullImage() {
        String imageName = "hello-world:latest";
        DockerContainerServiceImpl.PullImageCallback responseItemAdapter = containerService.pullImage(imageName);
        try {
            while (!responseItemAdapter.isCompleted(1, TimeUnit.SECONDS)) {
                TimeUnit.SECONDS.sleep(1);
            }
//            TimeUnit.SECONDS.sleep(100);
            assertTrue(containerService.hasImage(imageName));
            containerService.removeImage(imageName);
            assertFalse(containerService.hasImage(imageName));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRunCommand() {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            String command = "ls -al";
            containerService.start(containerInfo);
            ExecStartResultCallback execStartResultCallback = containerService.runCommand(containerInfo.getId(), command, out);
            execStartResultCallback.awaitCompletion();
            System.out.println("for what");
            assertTrue(new String(out.toByteArray()).startsWith("total"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (PortException e) {
            e.printStackTrace();
        } finally {
            if(containerInfo.getId() != null){
                System.out.println("stop");
                containerService.stop(containerInfo);
                containerService.remove(containerInfo);
            }
        }
    }

}
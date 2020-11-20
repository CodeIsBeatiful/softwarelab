package com.softwarelab.application;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;


@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@Slf4j
public abstract class AbstractBaseTest {

    public static final String DOCKER_HOST = "unix:///var/run/docker.sock";

    private static final String DOCKER_FILE_NAME="Dockerfile";

    private static final String POM_FILE_NAME="pom.xml";

    private static final String DEMO_NAME="demo";

    public static final String TEST_IMAGE_TAG = "softwarelab/demo:0.0.1";

    public static final String JAR_PATH = "/target/demo-0.01-SNAPSHOT.jar";

    private DockerClient dockerClient;

    private String basePath;

    public AbstractBaseTest() {
        DefaultDockerClientConfig config = DefaultDockerClientConfig
                .createDefaultConfigBuilder()
                .withDockerHost(DOCKER_HOST)
                .build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();

        dockerClient = DockerClientImpl.getInstance(config, httpClient);

        basePath = new File(this.getClass().getClassLoader().getResource(".").getFile()).getParentFile().getParentFile().getParent() + "/"+DEMO_NAME+"/";

    }


    public void checkTestImage() {
        //check image exists
        // docker build demo -f demo/Dockerfile -t test
        List<Image> images = dockerClient.listImagesCmd().withImageNameFilter(TEST_IMAGE_TAG).exec();
        if (images.size() == 0) {
            //mush build demo first
            //must install maven 3.0.0+
            if (!findJar()) {
                buildJar();
            }
            buildImage();
        }
    }

    protected boolean findJar() {
        return new File(basePath + JAR_PATH).exists();
    }

    protected void buildImage() {
        HashSet<String> sets = new HashSet<>();
        sets.add(TEST_IMAGE_TAG);
        dockerClient.buildImageCmd()
                .withBaseDirectory(new File(basePath))
                .withDockerfile(new File(basePath + DOCKER_FILE_NAME))
                .withTags(sets)
                .start()
                .awaitImageId();
    }

    protected void buildJar() {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(basePath + POM_FILE_NAME));
        request.setGoals(Collections.singletonList("package"));

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(System.getenv("M2_HOME")));

        try
        {
            invoker.execute( request );
        }
        catch (MavenInvocationException e)
        {
            e.printStackTrace();
        }
    }
}

package com.softwarelab.application;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.softwarelab.application.bean.ContainerInfo;
import com.softwarelab.application.bean.ContainerPortSetting;
import com.softwarelab.application.common.DbConst;
import com.softwarelab.application.entity.App;
import com.softwarelab.application.entity.AppVersion;
import com.softwarelab.application.entity.Instance;
import com.softwarelab.application.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@Slf4j
public abstract class AbstractBaseTest {

    public static final String DOCKER_HOST = "unix:///var/run/docker.sock";

    private static final String DOCKER_FILE_NAME="Dockerfile";

    private static final String POM_FILE_NAME="pom.xml";

    public static final String DEMO_NAME="demo";

    public static final String HELLO_WORLD_NAME="hello-world";

    public static final String LATEST_VERSION="latest";

    public static final String DEMO_VERSION ="0.0.1";

    public static final String DEMO_IMAGE="softwarelab/"+DEMO_NAME;

    public static final String DEMO_IMAGE_TAG = DEMO_IMAGE+":"+ DEMO_VERSION;

    public static final String JAR_PATH = "/target/demo-0.01-SNAPSHOT.jar";

    public static final String TEST_APP_TYPE="test";

    public static final int DEMO_TARGET_PORT=40001;

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

    public SysUser getDemoUser(){
        LocalDateTime now = LocalDateTime.now();
        SysUser sysUser = new SysUser();
        sysUser.setId(UUID.randomUUID().toString());
        sysUser.setUsername("test");
        sysUser.setPassword("123456");
        sysUser.setMail("test@test.com");
        sysUser.setCreateTime(now);
        sysUser.setUpdateTime(now);
        sysUser.setStatus(DbConst.STATUS_NORMAL);
        return sysUser;
    }

    public App getDemoApp(){
        LocalDateTime now = LocalDateTime.now();
        App app = new App();
        app.setAuthor("Blackstar");
        app.setName(DEMO_NAME);
        app.setDescription("Softwarelab app");
        app.setType(TEST_APP_TYPE);
        app.setCreateTime(now);
        app.setUpdateTime(now);
        app.setStatus(DbConst.STATUS_NORMAL);
        app.setLogo(getImageBytes());
        app.setAdditionalInfo("{\"imageName\":\"" + AbstractBaseTest.DEMO_IMAGE + "\",\"ports\":[{\"port\":8080,\"type\":\"http\",\"entrance\":true}]}");
        return app;
    }

    public AppVersion getDemoAppVersion(){
        LocalDateTime now = LocalDateTime.now();
        AppVersion appVersion = new AppVersion();
        appVersion.setAppName(DEMO_NAME);
        appVersion.setVersion(DEMO_VERSION);
        appVersion.setCreateTime(now);
        appVersion.setUpdateTime(now);
        appVersion.setStatus(DbConst.STATUS_NORMAL);
        appVersion.setDownloadStatus(DbConst.DOWNLOAD_STATUS_INIT);
        return appVersion;
    }

    public Instance getDemoInstance(){

        LocalDateTime now = LocalDateTime.now();
        Instance instance = new Instance();
        instance.setUserId(UUID.randomUUID().toString());
        instance.setName("my-softwarelab-demo");
        instance.setAppName(DEMO_NAME);
        instance.setAppVersion(DEMO_VERSION);
        instance.setCreateTime(now);
        instance.setUpdateTime(now);
        instance.setStatus(DbConst.STATUS_NORMAL);

        ContainerInfo containerInfo = ContainerInfo.builder()
                .ports(Arrays.asList(ContainerPortSetting.builder().targetPort(DEMO_TARGET_PORT).port(8080).entrance(true).type("http").build()))
                .envs(new ArrayList<>())
                .build();
        try {
            instance.setAdditionalInfo(new ObjectMapper().writeValueAsString(containerInfo));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public App getHelloWorldApp(){
        LocalDateTime now = LocalDateTime.now();
        App app = new App();
        app.setAuthor("Docker");
        app.setName(HELLO_WORLD_NAME);
        app.setDescription("Hello World! (an example of minimal Dockerization)");
        app.setType(TEST_APP_TYPE);
        app.setCreateTime(now);
        app.setUpdateTime(now);
        app.setStatus(DbConst.STATUS_NORMAL);
        app.setLogo(getImageBytes());
        app.setAdditionalInfo("{\"imageName\":\"" + AbstractBaseTest.HELLO_WORLD_NAME + "\"}");
        return app;
    }

    public AppVersion getHelloWorldAppVersion(){
        LocalDateTime now = LocalDateTime.now();
        AppVersion appVersion = new AppVersion();
        appVersion.setAppName(HELLO_WORLD_NAME);
        appVersion.setVersion(LATEST_VERSION);
        appVersion.setCreateTime(now);
        appVersion.setUpdateTime(now);
        appVersion.setStatus(DbConst.STATUS_NORMAL);
        appVersion.setDownloadStatus(DbConst.DOWNLOAD_STATUS_INIT);
        return appVersion;
    }


    private byte[] getImageBytes() {
        File file = new File(this.getClass().getClassLoader().getResource(".").getPath() + "/static/image/test.png");
        assertTrue(file.exists());
        try (FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
            ByteBuffer allocate = ByteBuffer.allocate((int) channel.size());
            channel.read(allocate);
            return allocate.array();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }




    public void checkTestImage() {
        //check image exists
        // docker build demo -f demo/Dockerfile -t test
        List<Image> images = dockerClient.listImagesCmd().withImageNameFilter(DEMO_IMAGE_TAG).exec();
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
        sets.add(DEMO_IMAGE_TAG);
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

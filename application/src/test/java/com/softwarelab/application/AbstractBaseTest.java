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
import com.softwarelab.application.bean.LoginRequest;
import com.softwarelab.application.common.DbConst;
import com.softwarelab.application.entity.App;
import com.softwarelab.application.entity.AppVersion;
import com.softwarelab.application.entity.Instance;
import com.softwarelab.application.entity.SysUser;
import com.softwarelab.application.service.IAppService;
import com.softwarelab.application.service.IAppVersionService;
import com.softwarelab.application.service.IInstanceService;
import com.softwarelab.application.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.*;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public abstract class AbstractBaseTest {

    public static final String SERVER = "localhost";

    public String AUTH_URL = "/api/auth/login";

    //docker
    public static final String DOCKER_HOST = "unix:///var/run/docker.sock";

    private static final String DOCKER_FILE_NAME = "Dockerfile";

    private static final String POM_FILE_NAME = "pom.xml";
    //app
    public static final String HELLO_WORLD_APP_NAME = "hello-world";

    public static final String LATEST_VERSION = "latest";

    public static final String HELLO_WORLD_INSTANCE_NAME = "my-" + HELLO_WORLD_APP_NAME;

    public static final String HELLO_WORLD_INSTANCE_ID = UUID.nameUUIDFromBytes(HELLO_WORLD_INSTANCE_NAME.getBytes()).toString();

    public static final String DEMO_APP_NAME = "demo";

    public static final String DEMO_APP_VERSION = "0.0.1";

    public static final String DEMO_APP_IMAGE = "softwarelab-" + DEMO_APP_NAME;

    public static final String DEMO_INSTANCE_NAME = "my-" + DEMO_APP_IMAGE;

    public static final String DEMO_INSTANCE_ID = UUID.nameUUIDFromBytes(DEMO_INSTANCE_NAME.getBytes()).toString();

    public static final Integer DEMO_INSTANCE_TARGET_PORT = 40001;

    public static final String DEMO_APP_IMAGE_TAG = DEMO_APP_IMAGE + ":" + DEMO_APP_VERSION;



    public static final String DEMO_JAR_PATH = "/target/demo-0.01-SNAPSHOT.jar";

    public static final String TEST_APP_TYPE = "test";

    //user
    public static final String TEST_USER_NAME = "test";

    public static final String TEST_USER_MAIL = "test@test.com";

    public static final String TEST_USER_PASSWORD = "123456";

    public static final String TEST_USER_ID = UUID.nameUUIDFromBytes(TEST_USER_NAME.getBytes()).toString();


    private DockerClient dockerClient;

    private String basePath;


    @Autowired
    private IAppService appService;

    @Autowired
    private IAppVersionService appVersionService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private IInstanceService instanceService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private RestTemplate restTemplate;

    public ObjectMapper objectMapper = new ObjectMapper();

    @LocalServerPort
    public int port;

    @Value("${test.useNetwork}")
    public boolean useNetwork;

    public String token;

    public static boolean isInit;

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
        basePath = new File(this.getClass().getClassLoader().getResource(".").getFile()).getParentFile().getParentFile().getParent() + "/" + DEMO_APP_NAME + "/";

    }


    @Before
    public void setUp() throws Exception {
        if(!isInit){
            checkTestImage();

            userService.save(getTestUser());

            appService.save(getHelloWorldApp());
            appVersionService.save(getHelloWorldAppVersion());
            instanceService.add(TEST_USER_ID,getHelloWorldInstance());

            appService.save(getDemoApp());
            appVersionService.save(getDemoAppVersion());
            instanceService.add(TEST_USER_ID,getDemoInstance());

            isInit = true;
        }


    }

    public void login() throws Exception {
        AUTH_URL = "http://" + SERVER + ":" + port + AUTH_URL;
        LoginRequest loginRequest = new LoginRequest(TEST_USER_NAME, TEST_USER_PASSWORD);
        ResponseEntity<Map> hashMapResponseEntity = restTemplate.postForEntity(AUTH_URL, objectMapper.writeValueAsString(loginRequest), Map.class);
        Map body = hashMapResponseEntity.getBody();
        token = ((Map) body.get("data")).get("token").toString();
    }

    @After
    public void tearDown() throws Exception {
    }

    public SysUser getTestUser() {
        LocalDateTime now = LocalDateTime.now();
        SysUser sysUser = new SysUser();
        sysUser.setId(TEST_USER_ID);
        sysUser.setUsername(TEST_USER_NAME);
        sysUser.setPassword(encoder.encode(TEST_USER_PASSWORD));
        sysUser.setMail(TEST_USER_MAIL);
        sysUser.setCreateTime(now);
        sysUser.setUpdateTime(now);
        sysUser.setStatus(DbConst.STATUS_NORMAL);
        return sysUser;
    }

    public App getDemoApp() {
        LocalDateTime now = LocalDateTime.now();
        App app = new App();
        app.setAuthor("Blackstar");
        app.setName(DEMO_APP_NAME);
        app.setDescription("Softwarelab app");
        app.setType(TEST_APP_TYPE);
        app.setCreateTime(now);
        app.setUpdateTime(now);
        app.setStatus(DbConst.STATUS_NORMAL);
        app.setLogo(getImageBytes());
        app.setAdditionalInfo("{\"imageName\":\"" + AbstractBaseTest.DEMO_APP_IMAGE + "\",\"ports\":[{\"port\":8080,\"type\":\"http\",\"entrance\":true}]}");
        return app;
    }

    public AppVersion getDemoAppVersion() {
        LocalDateTime now = LocalDateTime.now();
        AppVersion appVersion = new AppVersion();
        appVersion.setAppName(DEMO_APP_NAME);
        appVersion.setVersion(DEMO_APP_VERSION);
        appVersion.setCreateTime(now);
        appVersion.setUpdateTime(now);
        appVersion.setStatus(DbConst.STATUS_NORMAL);
        appVersion.setDownloadStatus(DbConst.DOWNLOAD_STATUS_INIT);
        return appVersion;
    }

    public Instance getDemoInstance() {

        LocalDateTime now = LocalDateTime.now();
        Instance instance = new Instance();
        instance.setUserId(TEST_USER_ID);
        instance.setId(DEMO_INSTANCE_ID);
        instance.setName(DEMO_INSTANCE_NAME);
        instance.setAppName(DEMO_APP_NAME);
        instance.setAppVersion(DEMO_APP_VERSION);
        instance.setCreateTime(now);
        instance.setUpdateTime(now);
        instance.setStatus(DbConst.STATUS_NORMAL);

        ContainerInfo containerInfo = ContainerInfo.builder()
                .ports(Arrays.asList(ContainerPortSetting.builder().targetPort(DEMO_INSTANCE_TARGET_PORT).port(8080).entrance(true).type("http").build()))
                .envs(new ArrayList<>())
                .build();
        try {
            instance.setAdditionalInfo(new ObjectMapper().writeValueAsString(containerInfo));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public App getHelloWorldApp() {
        LocalDateTime now = LocalDateTime.now();
        App app = new App();
        app.setAuthor("Docker");
        app.setName(HELLO_WORLD_APP_NAME);
        app.setDescription("Hello World! (an example of minimal Dockerization)");
        app.setType(TEST_APP_TYPE);
        app.setCreateTime(now);
        app.setUpdateTime(now);
        app.setStatus(DbConst.STATUS_NORMAL);
        app.setLogo(getImageBytes());
        app.setAdditionalInfo("{\"imageName\":\"" + AbstractBaseTest.HELLO_WORLD_APP_NAME + "\"}");
        return app;
    }

    public AppVersion getHelloWorldAppVersion() {
        LocalDateTime now = LocalDateTime.now();
        AppVersion appVersion = new AppVersion();
        appVersion.setAppName(HELLO_WORLD_APP_NAME);
        appVersion.setVersion(LATEST_VERSION);
        appVersion.setCreateTime(now);
        appVersion.setUpdateTime(now);
        appVersion.setStatus(DbConst.STATUS_NORMAL);
        appVersion.setDownloadStatus(DbConst.DOWNLOAD_STATUS_INIT);
        return appVersion;
    }

    public Instance getHelloWorldInstance() {

        LocalDateTime now = LocalDateTime.now();
        Instance instance = new Instance();
        instance.setUserId(TEST_USER_ID);
        instance.setId(HELLO_WORLD_INSTANCE_ID);
        instance.setName(HELLO_WORLD_INSTANCE_NAME);
        instance.setAppName(HELLO_WORLD_APP_NAME);
        instance.setAppVersion(LATEST_VERSION);
        instance.setCreateTime(now);
        instance.setUpdateTime(now);
        instance.setStatus(DbConst.STATUS_NORMAL);

        ContainerInfo containerInfo = ContainerInfo.builder()
                .ports(new ArrayList<>())
                .envs(new ArrayList<>())
                .build();
        try {
            instance.setAdditionalInfo(new ObjectMapper().writeValueAsString(containerInfo));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return instance;
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
        List<Image> images = dockerClient.listImagesCmd().withImageNameFilter(DEMO_APP_IMAGE_TAG).exec();
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
        return new File(basePath + DEMO_JAR_PATH).exists();
    }

    protected void buildImage() {
        HashSet<String> sets = new HashSet<>();
        sets.add(DEMO_APP_IMAGE_TAG);
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

        try {
            invoker.execute(request);
        } catch (MavenInvocationException e) {
            e.printStackTrace();
        }
    }
}

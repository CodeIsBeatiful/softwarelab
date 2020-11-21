package com.softwarelab.application.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.softwarelab.application.AbstractBaseTest;
import com.softwarelab.application.bean.AppInfo;
import com.softwarelab.application.entity.App;
import com.softwarelab.application.entity.AppVersion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;


@Transactional
public class AppControllerTest extends AbstractBaseTest {


    @Autowired
    private AppController appController;


    private App app;

    private AppVersion appVersion;

    @Before
    public void setUp() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        app = new App();
        app.setAuthor("blackstar");
        app.setName("hello-world");
        app.setDescription("Softwarelab app");
        app.setType("test");
        app.setCreateTime(now);
        app.setUpdateTime(now);
        app.setStatus(0);
        app.setLogo(getImageBytes());
        app.setAdditionalInfo("{\"imageName\":\"" + AbstractBaseTest.TEST_IMAGE_TAG + "\",\"ports\":[{\"port\":8080,\"type\":\"http\",\"entrance\":true}]}");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCRUD() {
        assertNotNull(appController.add(app));
        assertNotNull(appController.get(this.app.getName()));
        IPage<App> list = appController.list(0, 10, null);
        assertTrue(list.getSize() > 0);
        App update = new App();
        update.setName(this.app.getName());
        update.setAuthor("test");
        appController.update(update);
        update = appController.get(update.getName());
        assertNotNull(update);
        assertEquals(update.getAuthor(), "test");
        assertEquals(update.getType(), "test");
    }


    @Test
    public void testUpgrade() {
        //TODO
//        appController.upgrade();
    }

    @Test
    public void testLoad() {
        //TODO
//        String filePath = "/Users/blackstar/Downloads/softwarelab-source-0.0.1.zip";
//        try {
//            MultipartFile multipartFile = new MockMultipartFile("myfile",new FileInputStream(filePath));
//            appController.load(multipartFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }


    public byte[] getImageBytes() {
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


    @Test
    public void list() {
        IPage<App> pagedApps = appController.list(0, 1, "test");
        assertEquals(pagedApps.getPages(), 0);
        assertEquals(pagedApps.getRecords().size(), 0);
        appController.add(app);
        pagedApps = appController.list(0, 1, "test");
        assertEquals(pagedApps.getPages(), 1);
        assertEquals(pagedApps.getRecords().size(), 1);
        pagedApps = appController.list(0, 1, "All");
        assertTrue(pagedApps.getPages() >= 1);
        assertTrue(pagedApps.getRecords().size() >= 1);
    }

    @Test
    public void top() {
        appController.add(app);
        List<AppInfo> top = appController.top(1);
        assertTrue(top.size() > 0);
    }

    @Test
    public void getNameByType() {
        List<String> testApps = appController.getNameByType("test");
        assertTrue(testApps.size() == 0);
        appController.add(app);
        testApps = appController.getNameByType("test");
        assertTrue(testApps.size() == 1);
    }

    @Test
    public void upgrade() {
    }

    @Test
    public void load() {

    }
}
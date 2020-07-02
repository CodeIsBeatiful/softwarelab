package com.blackstar.softwarelab.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blackstar.softwarelab.AbstractBaseTest;
import com.blackstar.softwarelab.entity.App;
import com.blackstar.softwarelab.entity.AppVersion;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

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
        app.setAuthor("admin");
        app.setName("hello-world");
        app.setDescription("just for Software-lab");
        app.setType("test");
        app.setCreateTime(now);
        app.setUpdateTime(now);
        app.setStatus(0);

        appVersion = new AppVersion();
        appVersion.setAppName(this.app.getName());
        appVersion.setVersion("1.0");
        appVersion.setCreateTime(now);
        appVersion.setUpdateTime(now);
        appVersion.setStatus(0);


    }


    @Test
    public void testCRUD() {
        assertNotNull(appController.add(app));
        assertNotNull(appController.get(this.app.getName()));
        IPage<App> list = appController.list(0, 10);
        assertTrue(list.getSize() > 0);
        App update = new App();
        update.setName(this.app.getName());
        update.setAuthor("test");
        appController.update(update);
        update = appController.get(update.getName());
        assertNotNull(update);
        assertEquals(update.getAuthor(), "test");
        assertEquals(update.getType(), "test");
//        assertEquals(appController.remove(app.getId()), true);
//        assertEquals(appController.delete(app.getId()), true);
    }

    @Test
    public void testVersion(){
        appController.addVersion(appVersion);
        AppVersion version = appController.getVersion(this.app.getName(), this.appVersion.getVersion());
        assertNotNull(version);

    }



    @Test
    public void testGetImage(){
//        String id = "84c03026-166e-418c-9c33-0a02f5392020";
//        App app = appController.get(id);
//        app.setLogo(getImageBytes());
//        app.setLogoType(".png");
//        appController.update(app);
    }



    public byte[] getImageBytes(){
        File file = new File(this.getClass().getClassLoader().getResource(".").getPath()+ "/static/image/pg.png");
        assertTrue(file.exists());
        try(FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ)){
            ByteBuffer allocate = ByteBuffer.allocate((int) channel.size());
             channel.read(allocate);
             return allocate.array();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

}
package com.blackstar.softwarelab.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blackstar.softwarelab.app.entity.App;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class AppControllerTest {


    @Autowired
    private AppController appController;


    private App app;

    @Before
    public void setUp() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        app = new App();
        app.setId(UUID.randomUUID().toString());
        app.setAuthor("admin");
        app.setName("hello-world:latest");
        app.setDescription("Metabase is the easy, open source way for everyone in your company to ask questions and learn from data.");
        app.setType("bi");
        app.setCreateTime(now);
        app.setUpdateTime(now);
        app.setStatus(0);
        app.setLogo(getImageBytes());
        app.setLogoType(".png");


    }


    @Test
    public void testSave() {
        assertNotNull(appController.add(app));
        assertNotNull(appController.get(this.app.getId()));
        IPage<App> list = appController.list(0, 10);
        assertTrue(list.getSize() > 0);
        App update = new App();
        update.setId(this.app.getId());
        update.setAuthor("test");
        appController.update(update);
        update = appController.get(update.getId());
        assertNotNull(update);
        assertEquals(update.getAuthor(), "test");
        assertEquals(update.getType(), "bi");
        assertEquals(appController.remove(app.getId()), true);
        assertEquals(appController.delete(app.getId()), true);
    }



    @Test
    public void testGetImage(){
        String id = "84c03026-166e-418c-9c33-0a02f5392020";
        App app = appController.get(id);
        app.setLogo(getImageBytes());
        app.setLogoType(".png");
        appController.update(app);
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
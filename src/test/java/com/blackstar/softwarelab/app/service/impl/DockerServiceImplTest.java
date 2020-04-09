package com.blackstar.softwarelab.app.service.impl;

import com.blackstar.softwarelab.instance.bean.ContainerInfo;
import com.blackstar.softwarelab.instance.service.impl.DockerServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DockerServiceImplTest {


    @Autowired
    private DockerServiceImpl dockerServiceImpl;

    private ContainerInfo containerInfo;

    private int port;

    @Before
    public void setUp() throws Exception{
        port = 5444;
        List<String> ports = new ArrayList<>();
        //machine 5444, container inner port 5432
        ports.add(port+":5432");
        List<String> labels = new ArrayList<>();
        labels.add("user:admin");
        containerInfo = ContainerInfo.builder()
                .imageName("postgres:9.6")
                .name("admin_postgres_9.6")
                .ports(ports)
                .labels(labels)
                .build();
    }


    @Test
    public void test1(){
        dockerServiceImpl.start(containerInfo);
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Connection conn = getConn(port);

        assertNotNull(conn);
        closeConn(conn);

//        dockerServiceImpl.checkStatus(containerInfo);

        System.out.println(containerInfo.getStatus());

        dockerServiceImpl.stop(containerInfo);
        Connection newConn = getConn(port);
        assertNull(newConn);
        dockerServiceImpl.remove(containerInfo);

//        dockerServiceImpl.get(containerInfo);
    }

    private Connection getConn(int port){
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager
                    .getConnection("jdbc:postgresql://localhost:"+port+"/postgres",
                            "postgres", "postgres");


        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
        } catch (SQLException e) {
            //e.printStackTrace();
        } finally {
            return conn;
        }
    }

    private void closeConn(Connection conn){
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
package com.blackstar.softwarelab.service.impl;

import com.blackstar.softwarelab.AbstractBaseTest;
import com.blackstar.softwarelab.bean.ContainerInfo;
import com.blackstar.softwarelab.exception.PortException;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;


public class DockerContainerServiceImplTest extends AbstractBaseTest {


    @Autowired
    private DockerContainerServiceImpl dockerContainerServiceImpl;

    private ContainerInfo containerInfo;

    private int port;

    @Before
    public void setUp() throws Exception {
        port = 5444;
        List<String> ports = new ArrayList<>();
        //machine 5444, container inner port 5432
        ports.add(port + ":5432");
        List<String> labels = new ArrayList<>();
        labels.add("user:admin");
        List<String> envs = new ArrayList<>();
        envs.add("POSTGRES_PASSWORD=postgres");
        containerInfo = ContainerInfo.builder()
                .imageName("postgres:9.6")
                .name("admin_postgres_9.6")
                .ports(ports)
                .labels(labels)
                .envs(envs)
                .build();
    }


    @Test
    public void testStartAndStop() {
        try {
            dockerContainerServiceImpl.start(containerInfo);
        } catch (PortException e) {
            e.printStackTrace();
        }
        //get container
        Container container = dockerContainerServiceImpl.getContainer(containerInfo);
        assertNotNull(container);
        try {
            TimeUnit.SECONDS.sleep(5);
            Connection conn = getConn(port);
            assertNotNull(conn);
            closeConn(conn);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
//        dockerServiceImpl.checkStatus(containerInfo);
            System.out.println(containerInfo.getStatus());
            dockerContainerServiceImpl.stop(containerInfo);
            Connection newConn = getConn(port);
            assertNull(newConn);
            dockerContainerServiceImpl.remove(containerInfo);
//        dockerServiceImpl.get(containerInfo);
        }


    }


    @Test
    public void testImage() {
        assertTrue(dockerContainerServiceImpl.hasImage("postgres:9.6"));
    }

    @Test
    public void testPullImage() {
        String imageName = "hello-world:latest";
        ResultCallback.Adapter<PullResponseItem> responseItemAdapter = dockerContainerServiceImpl.pullImage(imageName);
        try {
            assertTrue(responseItemAdapter.awaitCompletion(60, TimeUnit.SECONDS));
            dockerContainerServiceImpl.removeImage(imageName);
            assertFalse(dockerContainerServiceImpl.hasImage(imageName));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRunCommand() {

        try (

                final ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {
            String command = "ls -al";
            ExecStartResultCallback execStartResultCallback = dockerContainerServiceImpl.runCommand("3c7ee75c286d", command, out);
            execStartResultCallback.awaitCompletion();
            System.out.println(new String(out.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Connection getConn(int port) {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager
                    .getConnection("jdbc:postgresql://localhost:" + port + "/postgres",
                            "postgres", "postgres");


        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
        } catch (SQLException e) {
            //e.printStackTrace();
        } finally {
            return conn;
        }
    }

    private void closeConn(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
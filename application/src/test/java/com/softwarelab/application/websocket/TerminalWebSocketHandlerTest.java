package com.softwarelab.application.websocket;

import com.softwarelab.application.service.IInstanceService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

public class TerminalWebSocketHandlerTest extends AbstractWebSocketBaseTest {

    private WebSocketClient webSocketClient;

    private String wsUrl="/api/ws/terminal/";

    @Autowired
    private IInstanceService instanceService;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        login();
        //start instance
        instanceService.start(TEST_USER_ID,DEMO_INSTANCE_ID);
        TimeUnit.SECONDS.sleep(1);
        wsUrl = "ws://" + SERVER + ":" + port + wsUrl+ DEMO_INSTANCE_ID + "?token=" + token;
        webSocketClient= WebSocketClient.getInstance(wsUrl);

    }


    @After
    public void tearDown() throws Exception {
        super.tearDown();
        //stop instance
        instanceService.stop(TEST_USER_ID,DEMO_INSTANCE_ID);
        instanceService.delete(DEMO_INSTANCE_ID);
    }

    @Test
    public void testTerminal() {
        try {
            webSocketClient.send("ls -al");
            TimeUnit.SECONDS.sleep(1);
            Assert.assertEquals(webSocketClient.getMessage(),"{\"type\":\"success\",\"detail\":\"service is connected\"}");
            Assert.assertNotNull(webSocketClient.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

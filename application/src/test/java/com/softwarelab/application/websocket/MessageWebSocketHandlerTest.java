package com.softwarelab.application.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class MessageWebSocketHandlerTest extends AbstractWebSocketBaseTest {

    private WebSocketClient webSocketClient;

    private String wsUrl = "/api/ws/message";

    @Before
    public void setUp() throws Exception {
        super.setUp();
        login();
        wsUrl = "ws://" + SERVER + ":" + port + wsUrl + "?token=" + token;
        webSocketClient = WebSocketClient.getInstance(wsUrl);

    }

    @Test
    public void testDownloadImage() {
        if(!useNetwork){
            return;
        }
        WebSocketRequestMessage webSocketRequestMessage = WebSocketRequestMessage.builder()
                .type("image")
                .operate("download")
                .content(HELLO_WORLD_APP_NAME +":"+LATEST_VERSION)
                .build();
        try {
            webSocketClient.send(objectMapper.writeValueAsString(webSocketRequestMessage));
            TimeUnit.SECONDS.sleep(10);
            Assert.assertNotNull(webSocketClient.getMessage());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

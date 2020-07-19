package com.blackstar.softwarelab.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
public class MessageWebSocketHandlerTest {


    private WebSocketClient webSocketClient;

    private String url="ws://localhost:8080/api/ws/message";

    @Before
    public void init() {
        webSocketClient= WebSocketClient.getInstance(url);
    }

    @Test
    public void testDownloadImage() {
        ObjectMapper objectMapper = new ObjectMapper();
        WebSocketRequestMessage webSocketRequestMessage = WebSocketRequestMessage.builder()
                .type("image")
                .operate("download")
                .content("hello-world:latest")
                .build();
        try {
            webSocketClient.send(objectMapper.writeValueAsString(webSocketRequestMessage));
            TimeUnit.SECONDS.sleep(30);
            Assert.assertNotNull(webSocketClient.getMessage());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package com.softwarelab.application.websocket;

import com.softwarelab.application.AbstractWebBaseTest;
import com.softwarelab.application.bean.LoginRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softwarelab.application.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MessageWebSocketHandlerTest extends AbstractWebBaseTest {

    private WebSocketClient webSocketClient;

    private String wsUrl = "/api/ws/message";

    @Before
    public void setUp() throws Exception {
        super.setUp();
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

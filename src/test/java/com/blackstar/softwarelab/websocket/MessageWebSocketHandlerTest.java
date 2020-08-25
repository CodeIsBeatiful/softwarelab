package com.blackstar.softwarelab.websocket;

import com.blackstar.softwarelab.bean.LoginRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
public class MessageWebSocketHandlerTest {

    @Value("${server.port:8080}")
    private int port;

    private String server = "localhost";

    private WebSocketClient webSocketClient;

    private String wsUrl = "/api/ws/message";

    private String authUrl = "/api/auth/login";

    @Autowired
    private RestTemplate restTemplate;

    ObjectMapper objectMapper = new ObjectMapper();


    @Before
    public void init() {
        wsUrl = "ws://" + server + ":" + port + wsUrl;
        authUrl = "http://" + server + ":" + port + authUrl;
        LoginRequest loginRequest = new LoginRequest("admin", "123456");
        try {
            ResponseEntity<Map> hashMapResponseEntity = restTemplate.postForEntity(authUrl, objectMapper.writeValueAsString(loginRequest), Map.class);
            Map body = hashMapResponseEntity.getBody();
            String token = ((Map) body.get("data")).get("token").toString();
            wsUrl += "?token=" + token;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        webSocketClient = WebSocketClient.getInstance(wsUrl);

    }

    @Test
    public void testDownloadImage() {


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

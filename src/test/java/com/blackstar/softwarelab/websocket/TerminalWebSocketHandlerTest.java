package com.blackstar.softwarelab.websocket;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
public class TerminalWebSocketHandlerTest {


    private WebSocketClient webSocketClient;

    private String url="ws://localhost:8080/api/ws/terminal/b66188e0-ca2e-4afa-9a9f-a17553f9020e";

    @Before
    public void init() {
        webSocketClient= WebSocketClient.getInstance(url);
    }

    @Test
    public void testTerminal() {

        try {
            webSocketClient.send("ls -al");
            TimeUnit.SECONDS.sleep(5);
            System.out.println(webSocketClient.getMessage());
            System.out.println(webSocketClient.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

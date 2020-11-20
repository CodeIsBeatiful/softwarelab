package com.softwarelab.application.websocket;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ConcurrentLinkedQueue;


@ClientEndpoint
public class WebSocketClient {
    private static Logger logger = LoggerFactory.getLogger(WebSocketClient.class);
    private Session session;

    private ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

    public String getMessage(){
        return queue.poll();
    }



    @OnOpen
    public void open(Session session) {
        logger.info("Client WebSocket is opening...");
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        queue.add(message);
        logger.info("Server send message: " + message);
    }

    @OnClose
    public void onClose() {
        logger.info("Websocket closed");
    }

    /**
     * 发送客户端消息到服务端
     *
     * @param message 消息内容
     */
    public void send(String message) {
        this.session.getAsyncRemote().sendText(message);
    }

    public static WebSocketClient getInstance(String url){
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        WebSocketClient webSocketClient = new WebSocketClient();
        try {
            container.connectToServer(webSocketClient, URI.create(url));
        } catch (DeploymentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return webSocketClient;
    }
}


package com.blackstar.softwarelab.websocket;


import com.blackstar.softwarelab.service.ContainerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class TerminalWebSocketHandler extends TextWebSocketHandler {

    private final ContainerService containerService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

    public TerminalWebSocketHandler(ContainerService containerService) {
        this.containerService = containerService;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        WebSocketResponseMessage successMessage = new WebSocketResponseMessage(WebSocketResponseMessage.Type.SUCCESS, "service is connected");
        session.sendMessage(new TextMessage(objectMapper.writeValueAsBytes(successMessage)));

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String command = message.getPayload();
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            //todo check container is exist and running
            //todo may be use instance id, get container id from db
            String path = session.getUri().getPath();
            String containerId = path.substring(path.lastIndexOf("/")+1);
            containerService.runCommand(containerId, command, byteArrayOutputStream).awaitCompletion();
            WebSocketResponseMessage successMessage = new WebSocketResponseMessage("success", new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8));
            session.sendMessage(new TextMessage(objectMapper.writeValueAsBytes(successMessage)));
        }


    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);

    }
}

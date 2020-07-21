package com.blackstar.softwarelab.websocket;


import com.blackstar.softwarelab.bean.ContainerInfo;
import com.blackstar.softwarelab.common.DbConst;
import com.blackstar.softwarelab.entity.Instance;
import com.blackstar.softwarelab.service.ContainerService;
import com.blackstar.softwarelab.service.IInstanceService;
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

    private final IInstanceService instanceService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

    public TerminalWebSocketHandler(IInstanceService instanceService, ContainerService containerService) {
        this.containerService = containerService;
        this.instanceService = instanceService;
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
            String path = session.getUri().getPath();
            String instanceId = path.substring(path.lastIndexOf("/") + 1);
            Instance instance = instanceService.getById(instanceId);
            ContainerInfo containerInfo = objectMapper.readValue(instance.getAdditionalInfo(), ContainerInfo.class);
            String containerId = containerInfo.getId();
            //todo
            containerService.runCommand(containerId, command, byteArrayOutputStream).awaitCompletion();
            WebSocketResponseMessage successMessage = new WebSocketResponseMessage(WebSocketResponseMessage.Type.SUCCESS, new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8));
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

package com.blackstar.softwarelab.websocket;


import com.blackstar.softwarelab.bean.ContainerInfo;
import com.blackstar.softwarelab.entity.Instance;
import com.blackstar.softwarelab.service.ContainerService;
import com.blackstar.softwarelab.service.IInstanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TerminalWebSocketHandler extends TextWebSocketHandler {

    private final ContainerService containerService;

    private final IInstanceService instanceService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private ConcurrentLinkedQueue<WebSessionAndCallback> callbackQueue = new ConcurrentLinkedQueue<>();

    public TerminalWebSocketHandler(IInstanceService instanceService, ContainerService containerService) {
        this.containerService = containerService;
        this.instanceService = instanceService;
        new Thread(() -> {
            for (; ; ) {
                try {
                    WebSessionAndCallback webSessionAndCallback = callbackQueue.poll();
                    if (webSessionAndCallback == null) {
                        TimeUnit.MILLISECONDS.sleep(500);
                        continue;
                    }
                    if(webSessionAndCallback.callback.awaitCompletion(50,TimeUnit.MILLISECONDS)){
                        String message = new String(webSessionAndCallback.byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
                        webSessionAndCallback.byteArrayOutputStream.close();
                        WebSocketResponseMessage successMessage = new WebSocketResponseMessage(WebSocketResponseMessage.Type.SUCCESS, message);
                        WebSocketSession webSocketSession = webSessionAndCallback.webSocketSession;
                        if(webSocketSession.isOpen()){
                            webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsBytes(successMessage)));
                        }
                    }else{
                        callbackQueue.add(webSessionAndCallback);
                    }
                } catch (Exception e) {
                    log.error("init terminal websocket handler error",e);
                }
            }

        }, "check terminal thread").start();
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
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        String path = session.getUri().getPath();
        String instanceId = path.substring(path.lastIndexOf("/") + 1);
        Instance instance = instanceService.getById(instanceId);
        ContainerInfo containerInfo = objectMapper.readValue(instance.getAdditionalInfo(), ContainerInfo.class);
        String containerId = containerInfo.getId();
        callbackQueue.offer(WebSessionAndCallback.builder()
                .webSocketSession(session)
                .callback(containerService.runCommand(containerId, command, byteArrayOutputStream))
                .byteArrayOutputStream(byteArrayOutputStream)
                .build());
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);

    }

    @Builder
    @Data
    public static class WebSessionAndCallback {
        private WebSocketSession webSocketSession;

        private ExecStartResultCallback callback;

        private ByteArrayOutputStream byteArrayOutputStream;
    }

}

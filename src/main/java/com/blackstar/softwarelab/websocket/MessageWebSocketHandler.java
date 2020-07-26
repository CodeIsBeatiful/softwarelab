package com.blackstar.softwarelab.websocket;


import com.blackstar.softwarelab.service.ContainerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

public class MessageWebSocketHandler extends TextWebSocketHandler {


    private final ImageChecker imageChecker;

    private final ContainerService containerService;

    private ObjectMapper objectMapper = new ObjectMapper();

    public MessageWebSocketHandler(ImageChecker imageChecker, ContainerService containerService) {
        this.imageChecker = imageChecker;
        this.containerService = containerService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        WebSocketRequestMessage webSocketRequestMessage = objectMapper.readValue(message.getPayload(), WebSocketRequestMessage.class);

        switch (webSocketRequestMessage.getType()) {
            case "image":
                processImageMessage(session,webSocketRequestMessage);
                break;
            default:
                break;
        }



    }

    private void processImageMessage(WebSocketSession session, WebSocketRequestMessage message) {

        switch (message.getOperate()){
            case "download":
                processImageDownload(session,message.getContent());
                break;
            default:
                break;
        }

    }

    private void processImageDownload(WebSocketSession session, String imageName) {
        WebSocketResponseMessage responseMessage = null;
        if (!containerService.hasImage(imageName)) {
            imageChecker.add(imageName,WebSocketSessionAndCallback
                    .builder()
                    .webSocketSession(session)
            .callback(containerService.pullImage(imageName))
            .build());
            responseMessage = new WebSocketResponseMessage("success", imageName+"开始下载");
        }else{
            responseMessage = new WebSocketResponseMessage("success", imageName+"已存在");
        }
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsBytes(responseMessage)));
        } catch (IOException e) {
            e.printStackTrace();
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
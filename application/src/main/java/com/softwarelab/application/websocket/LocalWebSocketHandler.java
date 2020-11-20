package com.softwarelab.application.websocket;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class LocalWebSocketHandler extends TextWebSocketHandler {


    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        WebSocketResponseMessage successMessage = new WebSocketResponseMessage(WebSocketResponseMessage.Type.SUCCESS, "service is connected");
        session.sendMessage(new TextMessage(objectMapper.writeValueAsBytes(successMessage)));

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"bash", "-c", message.getPayload()});
            process.waitFor();
            InputStream inputStream = process.getInputStream();
            InputStream errorStream = process.getErrorStream();
            if (errorStream.available() > 0) {
                byte[] bytes = new byte[errorStream.available()];
                errorStream.read(bytes);
                WebSocketResponseMessage errorMessage = new WebSocketResponseMessage("error", new String(bytes, StandardCharsets.UTF_8));
                session.sendMessage(new TextMessage(objectMapper.writeValueAsBytes(errorMessage)));
            } else {
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                WebSocketResponseMessage successMessage = new WebSocketResponseMessage("success", new String(bytes, StandardCharsets.UTF_8));
                session.sendMessage(new TextMessage(objectMapper.writeValueAsBytes(successMessage)));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (process != null) {
                process.destroy();
            }
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

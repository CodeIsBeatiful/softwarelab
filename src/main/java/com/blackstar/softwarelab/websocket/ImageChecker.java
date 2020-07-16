package com.blackstar.softwarelab.websocket;

import com.blackstar.softwarelab.websocket.WebSocketResponseMessage;
import com.blackstar.softwarelab.websocket.WebSocketSessionAndCallback;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author blackstar
 * <p>
 * image checker
 */
@Component
@Slf4j
public class ImageChecker {

    private Map<String, WebSocketSessionAndCallback> pullImageMap = new ConcurrentHashMap<>();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${checker.sleepMillSeconds:500}")
    private int sleepMillSeconds;

    @PostConstruct
    public void init() {
        new Thread(() -> {
            try {
                for (; ; ) {
                    checkImage();
                    TimeUnit.MILLISECONDS.sleep(sleepMillSeconds);
                }
            } catch (InterruptedException e) {
                log.error("checker Interrupted", e);
            }
        }, "checkImageThread").start();
    }

    public boolean add(String imageName, WebSocketSessionAndCallback webSessionAndCallback) {
        if (pullImageMap.get(imageName) != null) {
            return false;
        }
        pullImageMap.put(imageName, webSessionAndCallback);
        return true;
    }


    private void checkImage() {
        pullImageMap.forEach((imageName, webSessionAndCallback) -> {
            try {
                if (webSessionAndCallback.getCallback().awaitCompletion(1, TimeUnit.SECONDS)) {
                    pullImageMap.remove(imageName);
                    //use websocket
                    WebSocketSession webSocketSession = webSessionAndCallback.getWebSocketSession();
                    if (webSocketSession.isOpen()) {
                        WebSocketResponseMessage successMessage = new WebSocketResponseMessage("success", imageName + "下载完毕");
                        webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsBytes(successMessage)));
                    }
                }
            } catch (InterruptedException e) {
                log.error("check image error:", e);
            } catch (JsonProcessingException e) {
                log.error("write success message error", e);
            } catch (IOException e) {
                log.error("write success message error", e);
            }
        });
    }


}

package com.softwarelab.application.websocket;

import com.softwarelab.application.service.impl.DockerContainerServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@AllArgsConstructor
@Data
@Builder
public class WebSocketSessionAndCallback {

    private WebSocketSession webSocketSession;

    private DockerContainerServiceImpl.PullImageCallback callback;


}


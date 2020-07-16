package com.blackstar.softwarelab.websocket;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.PullResponseItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@AllArgsConstructor
@Data
@Builder
public class WebSocketSessionAndCallback {
    private WebSocketSession webSocketSession;

    private ResultCallback.Adapter<PullResponseItem> callback;


}


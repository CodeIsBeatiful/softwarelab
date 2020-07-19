package com.blackstar.softwarelab.websocket;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebSocketResponseMessage {

    //success or failed
    private String type;

    private String detail;


    public static class Type {
        public static final String SUCCESS = "success";

        public static final String FAILED = "failed";
    }
}

package com.softwarelab.application.config;

import com.softwarelab.application.bean.SecurityUser;
import com.softwarelab.application.checker.ImageChecker;
import com.softwarelab.application.entity.Instance;
import com.softwarelab.application.service.*;
import com.softwarelab.application.websocket.MessageWebSocketHandler;
import com.softwarelab.application.websocket.TerminalWebSocketHandler;
import com.softwarelab.application.common.DbConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    public static final String WS_PREFIX = "/api/ws/";

    private static final String WS_TERMINAL_MAPPING = WS_PREFIX + "/terminal/*";

    private static final String WS_MESSAGE_MAPPING = WS_PREFIX + "/message";


    @Autowired
    private ContainerService containerService;

    @Autowired
    private IInstanceService instanceService;

    @Autowired
    private ImageChecker imageChecker;

    @Autowired
    private IAppService appService;

    @Autowired
    private IAppVersionService appVersionService;

    @Autowired
    private IAppSourceService appSourceService;

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(32768);
        container.setMaxBinaryMessageBufferSize(32768);
        return container;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(newTerminalHandler(), WS_TERMINAL_MAPPING)
                .addHandler(newMessageHandler(), WS_MESSAGE_MAPPING)
                .setAllowedOrigins("*")
                .addInterceptors(new HttpSessionHandshakeInterceptor(), new HandshakeInterceptor() {

                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                                   Map<String, Object> attributes) throws Exception {
                        SecurityUser user = getCurrentUser();
                        if (user == null) {
                            response.setStatusCode(HttpStatus.UNAUTHORIZED);
                            return false;
                        } else {
                            if (((ExceptionWebSocketHandlerDecorator) wsHandler).getLastHandler() instanceof TerminalWebSocketHandler) {
                                return handshakeTerminal(request, instanceService);
                            }
                            return true;
                        }
                    }

                    private boolean handshakeTerminal(ServerHttpRequest request, IInstanceService instanceService) {
                        String path = request.getURI().getPath();
                        String instanceId = path.substring(path.lastIndexOf("/") + 1);
                        Instance instance = instanceService.getById(instanceId);
                        if (instance == null) {
                            return false;
                        }
                        if (instance.getRunningStatus() != DbConst.RUNNING_STATUS_START) {
                            return false;
                        }
                        return true;
                    }


                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                               Exception exception) {
                        //do nothing
                    }
                });
    }

    @Bean
    public TerminalWebSocketHandler newTerminalHandler() {
        return new TerminalWebSocketHandler(instanceService, containerService);
    }

    @Bean
    public MessageWebSocketHandler newMessageHandler() {
        return new MessageWebSocketHandler(imageChecker, containerService, appService, appVersionService, appSourceService);
    }

    protected SecurityUser getCurrentUser() {
        return SecurityUser.builder().build();
    }
}

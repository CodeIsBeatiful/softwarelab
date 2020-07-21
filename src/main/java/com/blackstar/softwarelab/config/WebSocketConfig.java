package com.blackstar.softwarelab.config;

import com.blackstar.softwarelab.common.DbConst;
import com.blackstar.softwarelab.entity.Instance;
import com.blackstar.softwarelab.security.SecurityUser;
import com.blackstar.softwarelab.service.ContainerService;
import com.blackstar.softwarelab.service.IInstanceService;
import com.blackstar.softwarelab.service.ISysUserService;
import com.blackstar.softwarelab.websocket.ImageChecker;
import com.blackstar.softwarelab.websocket.MessageWebSocketHandler;
import com.blackstar.softwarelab.websocket.TerminalWebSocketHandler;
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
    private ISysUserService userService;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private IInstanceService instanceService;

    @Autowired
    private ImageChecker imageChecker;


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
                                return handshakeTerminal(request,instanceService);
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
        return new MessageWebSocketHandler(imageChecker, containerService);
    }

    protected SecurityUser getCurrentUser() {
        //TODO mock data
//        SysUser user = userService.getById("205635b9-ab37-43cb-82c2-811a58880fa1");
//        return SecurityUser.builder()
//                .id(user.getId())
//                .mail(user.getMail())
//                .username(user.getUsername())
//                .build();

        return SecurityUser.builder().build();
    }
}

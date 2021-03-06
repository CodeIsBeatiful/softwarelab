package com.softwarelab.application.websocket;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softwarelab.application.bean.ContainerSetting;
import com.softwarelab.application.checker.ImageChecker;
import com.softwarelab.application.common.DbConst;
import com.softwarelab.application.entity.App;
import com.softwarelab.application.entity.AppSource;
import com.softwarelab.application.entity.AppVersion;
import com.softwarelab.application.service.ContainerService;
import com.softwarelab.application.service.IAppService;
import com.softwarelab.application.service.IAppSourceService;
import com.softwarelab.application.service.IAppVersionService;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

public class MessageWebSocketHandler extends TextWebSocketHandler {


    private final ImageChecker imageChecker;

    private final ContainerService containerService;

    private final IAppService appService;

    private final IAppVersionService appVersionService;

    private final IAppSourceService appSourceService;

    private ObjectMapper objectMapper = new ObjectMapper();

    public MessageWebSocketHandler(ImageChecker imageChecker, ContainerService containerService, IAppService appService, IAppVersionService appVersionService, IAppSourceService appSourceService) {
        this.imageChecker = imageChecker;
        this.containerService = containerService;
        this.appService = appService;
        this.appVersionService = appVersionService;
        this.appSourceService = appSourceService;
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
                processImageMessage(session, webSocketRequestMessage);
                break;
            case "app":
                processAppMessage(session, webSocketRequestMessage);
            default:
                break;
        }


    }

    private void processImageMessage(WebSocketSession session, WebSocketRequestMessage message) {
        switch (message.getOperate()) {
            case "download":
                processImageDownload(session, message.getContent());
                break;
            default:
                break;
        }
    }

    private void processAppMessage(WebSocketSession session, WebSocketRequestMessage message) throws Exception{
        switch (message.getOperate()) {
            case "upgrade":
                processAppUpgrade(session, message.getContent());
                break;
            case "reload":
                processAppReload(session, message.getContent());
                break;
            default:
                break;
        }
    }

    private void processImageDownload(WebSocketSession session, String content) {
        String[] split = content.split(":");
        if (split.length != 2) {
            //
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsBytes(new WebSocketResponseMessage(WebSocketResponseMessage.Type.FAILED, content + "不是有效的镜像名称"))));
            } catch (IOException e) {
                // do nothing
            }

        }
        AppVersion appVersion = AppVersion.builder().appName(split[0]).version(split[1]).build();

        WebSocketResponseMessage responseMessage = null;
        App app = appService.getByName(appVersion.getAppName());
        String imageName = null;
        try {
            ContainerSetting containerSetting = objectMapper.readValue(app.getAdditionalInfo(), ContainerSetting.class);
            imageName = containerSetting.getImageName();
            if (imageName == null) {
                responseMessage = new WebSocketResponseMessage("failed", content + " can't find image");
                session.sendMessage(new TextMessage(objectMapper.writeValueAsBytes(responseMessage)));
            }
        } catch (IOException e) {
            //do nothing
        }
        if (!containerService.hasImage(appVersion.getAppName() + ":" + appVersion.getVersion())) {
            imageChecker.add(appVersion, WebSocketSessionAndCallback.builder()
                    .webSocketSession(session)
                    .callback(containerService.pullImage(imageName + ":" + appVersion.getVersion()))
                    .build());
            responseMessage = new WebSocketResponseMessage("success", content + " begin download");
        } else {
            appVersionService.update(new UpdateWrapper<AppVersion>()
                    .eq(DbConst.COLUMN_APP_NAME, appVersion.getAppName())
                    .eq(DbConst.COLUMN_VERSION, appVersion.getVersion())
                    .set(DbConst.COLUMN_DOWNLOAD_STATUS, DbConst.DOWNLOAD_STATUS_FINISH));
            responseMessage = new WebSocketResponseMessage("success", content + " is exist");
        }
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsBytes(responseMessage)));
        } catch (IOException e) {
            // do nothing
        }

    }

    public void processAppUpgrade(WebSocketSession session, String content) throws Exception{
        String detail;
        AppSource appSource = appSourceService.list().get(0);
        if (appSource.getStatus() > 0) {
            detail = "App store is Upgrading or reloading";
        } else {
            detail = "Upgrade " + (appSourceService.upgrade() ? "success" : "failed");
        }
        WebSocketResponseMessage responseMessage = new WebSocketResponseMessage("success", detail);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsBytes(responseMessage)));
    }

    public void processAppReload(WebSocketSession session, String content) throws Exception{
        String detail;
        AppSource appSource = appSourceService.list().get(0);
        if (appSource.getStatus() > 0) {
            detail = "App store is Upgrading or reloading";
        } else {
            detail = "Reload " + (appSourceService.loadToDb() ? "success" : "failed");
        }
        WebSocketResponseMessage responseMessage = new WebSocketResponseMessage("success", detail);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsBytes(responseMessage)));
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

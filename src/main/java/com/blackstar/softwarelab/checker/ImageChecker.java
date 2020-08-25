package com.blackstar.softwarelab.checker;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.blackstar.softwarelab.common.DbConst;
import com.blackstar.softwarelab.entity.App;
import com.blackstar.softwarelab.entity.AppVersion;
import com.blackstar.softwarelab.service.ContainerService;
import com.blackstar.softwarelab.service.IAppVersionService;
import com.blackstar.softwarelab.websocket.WebSocketResponseMessage;
import com.blackstar.softwarelab.websocket.WebSocketSessionAndCallback;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
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

    private Map<AppVersion, WebSocketSessionAndCallback> pullImageMap = new ConcurrentHashMap<>();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${checker.sleepMillSeconds:500}")
    private int sleepMillSeconds;

    @Autowired
    private IAppVersionService appVersionService;

    @Autowired
    private ContainerService containerService;


    @PostConstruct
    public void init() {
        //if restart ,process app versions in the download begin status
        processBeginDownloadImage();
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

    private void processBeginDownloadImage() {
        List<AppVersion> appVersions = appVersionService.list(new QueryWrapper<AppVersion>().eq(DbConst.COLUMN_DOWNLOAD_STATUS, DbConst.DOWNLOAD_STATUS_BEGIN));
        if (appVersions != null && appVersions.size() > 0) {
            appVersions.forEach(appVersion -> {
                int status = DbConst.DOWNLOAD_STATUS_INIT;
                if (containerService.hasImage(appVersion.getAppName() + ":" + appVersion.getVersion())) {
                    status = DbConst.DOWNLOAD_STATUS_FINISH;
                }
                updateStatus(appVersion, status);
            });
        }
    }


    public boolean add(AppVersion appVersion, WebSocketSessionAndCallback webSessionAndCallback) {
        if (pullImageMap.get(appVersion) != null) {
            return false;
        }
        pullImageMap.put(appVersion, webSessionAndCallback);
        // set version status to download begin
        updateStatus(appVersion, DbConst.DOWNLOAD_STATUS_BEGIN);
        return true;
    }


    private void checkImage() {
        pullImageMap.forEach((appVersion, webSessionAndCallback) -> {
            try {
                if (webSessionAndCallback.getCallback().awaitCompletion(1, TimeUnit.SECONDS)) {

                    pullImageMap.remove(appVersion);
                    // set version status to download finish
                    updateStatus(appVersion, DbConst.DOWNLOAD_STATUS_FINISH);
                    //use websocket
                    WebSocketSession webSocketSession = webSessionAndCallback.getWebSocketSession();
                    if (webSocketSession.isOpen()) {
                        WebSocketResponseMessage successMessage = new WebSocketResponseMessage("success", appVersion.getAppName() + ":" + appVersion.getVersion() + "下载完成");
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

    private void updateStatus(AppVersion appVersion, int status) {
        appVersionService.update(new UpdateWrapper<AppVersion>()
                .set(DbConst.COLUMN_DOWNLOAD_STATUS, status)
                .eq("app_name", appVersion.getAppName())
                .eq("version", appVersion.getVersion()));
    }


}

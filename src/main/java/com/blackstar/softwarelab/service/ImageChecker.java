package com.blackstar.softwarelab.service;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.PushResponseItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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

    private Map<String, ResultCallback.Adapter<PushResponseItem>> pullImageMap = new ConcurrentHashMap<>();

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

    public boolean add(String imageName, ResultCallback.Adapter<PushResponseItem> callback) {
        if (pullImageMap.get(imageName) != null) {
            return false;
        }
        pullImageMap.put(imageName, callback);
        return true;
    }


    private void checkImage() {
        pullImageMap.forEach((imageName, callback) -> {
            try {
                if (callback.awaitCompletion(1, TimeUnit.SECONDS)) {
                    pullImageMap.remove(imageName);
                }
            } catch (InterruptedException e) {
                log.error("check image error:" + e);
            }
        });
    }


}

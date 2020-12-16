package com.softwarelab.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket
@EnableTransactionManagement
public class SoftwareLabApplication {


    private static final String LOG_PATH = "--log.path";

    private static final String DEFAULT_LOG_PATH = "/logs";

    private static final String DATA_PATH = "--data.path";

    public static void main(String[] args) {
        args = process(args);
        SpringApplication.run(SoftwareLabApplication.class, args);
    }

    private static String[] process(String[] args) {
        // scan arg
        String logArg = null;
        String dataArg = null;
        for (String arg : args) {
            if (arg.startsWith(LOG_PATH)) {
                logArg = arg;
            } else if (arg.startsWith(DATA_PATH)) {
                dataArg = arg;
            }
        }
        //process log arg
        if (logArg != null) {
            System.setProperty("log.path", logArg.split("=")[1]);
        } else {
            System.setProperty("log.path", System.getProperty("user.dir") + DEFAULT_LOG_PATH);
        }
        //process data arg
        if (dataArg != null) {
            System.setProperty("data.path", logArg.split("=")[1]);
        } else {
            System.setProperty("data.path", System.getProperty("user.dir"));
        }


        return args;

    }

}

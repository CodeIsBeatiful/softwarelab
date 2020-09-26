package com.blackstar.softwarelab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

@SpringBootApplication
@Transactional
public class SoftwareLabApplication {


    private static final String LOG_PATH = "--log.path";

    private static final String DEFAULT_LOG_PATH = "/logs";

    public static void main(String[] args) {
        args = process(args);
        SpringApplication.run(SoftwareLabApplication.class, args);
    }

    private static String[] process(String[] args) {
        // process
        //log_path set
        Optional<String> logArgs = Arrays.stream(args).filter(arg -> arg.startsWith(LOG_PATH)).findFirst();

        if (logArgs.isPresent()) {
            System.setProperty("log.path", logArgs.get().split("=")[1]);
        } else {
            System.setProperty("log.path", System.getProperty("user.dir")+DEFAULT_LOG_PATH);
        }
        return args;

    }

}

package com.blackstar.softwarelab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
@Transactional
public class SoftwareLabApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoftwareLabApplication.class, args);
    }

}

package com.softwarelab.demo.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {


    @RequestMapping("/")
    public String hello(){
        return "hello,softwarelab";
    }
}

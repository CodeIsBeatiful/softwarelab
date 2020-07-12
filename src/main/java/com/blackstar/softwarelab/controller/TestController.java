package com.blackstar.softwarelab.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/hello")
    public String sayHello(){
        return "ok";
    }


    @RequestMapping("/exp")
    public String exception(){
        throw new RuntimeException("test runtime exception,need  catch it");
    }



}

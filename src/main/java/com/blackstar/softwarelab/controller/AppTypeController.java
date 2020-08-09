package com.blackstar.softwarelab.controller;

import com.blackstar.softwarelab.common.KeyValuePair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/appTypes")
public class AppTypeController {

    @RequestMapping(method = RequestMethod.GET)
    public List<KeyValuePair> list(){
        ArrayList<KeyValuePair> keyValuePairs = new ArrayList<>();
        keyValuePairs.add(new KeyValuePair("All",""));
        keyValuePairs.add(new KeyValuePair("Util","Util"));
        keyValuePairs.add(new KeyValuePair("DataBase","DataBase"));
        keyValuePairs.add(new KeyValuePair("BI","BI"));
        return keyValuePairs;
    }

}

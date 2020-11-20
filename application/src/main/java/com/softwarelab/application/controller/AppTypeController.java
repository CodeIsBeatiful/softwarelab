package com.softwarelab.application.controller;

import com.softwarelab.application.common.KeyValuePair;
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
        keyValuePairs.add(new KeyValuePair("All","All"));
        keyValuePairs.add(new KeyValuePair("Util","Util"));
        keyValuePairs.add(new KeyValuePair("Database","Database"));
        keyValuePairs.add(new KeyValuePair("BI","BI"));
        keyValuePairs.add(new KeyValuePair("Web","Web"));
        keyValuePairs.add(new KeyValuePair("MQ","MQ"));
        keyValuePairs.add(new KeyValuePair("Cache","Cache"));
        keyValuePairs.add(new KeyValuePair("Other","Other"));
        return keyValuePairs;
    }

}

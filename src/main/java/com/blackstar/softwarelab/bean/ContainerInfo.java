package com.blackstar.softwarelab.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ContainerInfo {


    private String id;

    private String name;

    private String imageName;

    private String status;


    //e.g. pg 5444:5432
    private List<String> ports;

    //e.g. user:abc
    private List<String> labels;


}

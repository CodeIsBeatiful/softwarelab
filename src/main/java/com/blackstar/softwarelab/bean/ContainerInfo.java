package com.blackstar.softwarelab.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Serialization for the Instance additionalInfo property
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ContainerInfo {


    private String id;

    private String name;

    private String imageName;

    private String status;

    //e.g. pg 5444:5432, :5432
    private List<String> ports;

    //e.g. user:abc
    private List<String> labels;

    //e.g. user=123456
    private List<String> envs;

    //e.g. http://ip:port/index.html
    private String url;


    public ContainerInfo self() {
        return this;
    }

}

package com.blackstar.softwarelab.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContainerInfo {


    private String id;

    private String name;

    private String imageName;

    private String status;

    //e.g. pg 5444:5432, :5432
    private List<ContainerPortSetting> ports;

    //e.g. user:abc
    private List<String> labels;

    //e.g. user=123456
    private List<ContainerEnvSetting> envs;

    private String url;
    //e.g. http://ip:port/index.html
    private String home;


    public ContainerInfo self() {
        return this;
    }

}

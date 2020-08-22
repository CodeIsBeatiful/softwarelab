package com.blackstar.softwarelab.bean;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  Serialization for the App and AppVersion additionalInfo property
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContainerPortSetting {


    private int port;

    private Integer targetPort;

    // http/mqtt
    private String type;

    // true/false
    // Used when the type is http
    private boolean entrance;
    //description
    private String desc;

}

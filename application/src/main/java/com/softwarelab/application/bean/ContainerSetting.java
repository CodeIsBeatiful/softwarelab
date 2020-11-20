package com.softwarelab.application.bean;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *  Serialization for the App and AppVersion additionalInfo property
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContainerSetting {


    private String imageName;
    //
    private List<ContainerPortSetting> ports;
    //e.g. /index.html
    private String url;

}

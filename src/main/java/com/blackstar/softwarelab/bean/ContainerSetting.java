package com.blackstar.softwarelab.bean;


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
public class ContainerSetting {


    private String imageName;

    private int[] ports;

}

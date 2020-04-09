package com.blackstar.softwarelab.instance.bean;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ContainerSetting {


    private String imageName;

    private int[] ports;





}

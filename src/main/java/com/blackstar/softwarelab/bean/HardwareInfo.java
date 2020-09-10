package com.blackstar.softwarelab.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HardwareInfo {

    private long ts;

    private String cpu;

    private String memory;
}

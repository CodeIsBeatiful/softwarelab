package com.blackstar.softwarelab.bean;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SortObj {

    private String key;

    private String order;

    private String value;
}

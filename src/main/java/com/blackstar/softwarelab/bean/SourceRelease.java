package com.blackstar.softwarelab.bean;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SourceRelease {


    @JsonProperty("tag_name")
    private String tagName;

    @JsonProperty("zipball_url")
    private String zipballUrl;
}

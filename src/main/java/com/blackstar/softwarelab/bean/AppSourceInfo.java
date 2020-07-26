
package com.blackstar.softwarelab.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppSourceInfo {

    @JsonProperty("additional_info")
    private JsonNode additionalInfo;
    private String description;
    private String type;
    private List<AppSourceVersion> versions;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppSourceVersion {

        private String version;
        @JsonProperty("additional_info")
        private JsonNode additionalInfo;
    }

}

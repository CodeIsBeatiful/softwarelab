package com.softwarelab.application.bean;

import com.softwarelab.application.entity.App;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppInfo extends App {

    private Integer usedCount;

}

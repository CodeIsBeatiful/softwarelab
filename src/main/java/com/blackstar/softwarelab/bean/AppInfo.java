package com.blackstar.softwarelab.bean;

import com.blackstar.softwarelab.entity.App;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppInfo extends App {

    private Integer usedCount;

}

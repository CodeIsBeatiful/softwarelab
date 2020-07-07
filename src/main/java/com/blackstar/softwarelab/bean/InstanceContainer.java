package com.blackstar.softwarelab.bean;

import com.blackstar.softwarelab.entity.Instance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InstanceContainer extends Instance {

    private ContainerInfo containerInfo;

}

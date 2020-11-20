package com.softwarelab.application.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author blackstar
 * @since 2020-03-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Instance implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String userId;

    private String name;

    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer status;

    private String appName;

    private String appVersion;

    private String additionalInfo;

    private Integer runningStatus;


}

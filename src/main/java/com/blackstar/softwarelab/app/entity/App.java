package com.blackstar.softwarelab.app.entity;

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
 * @since 2020-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class App implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String author;

    private String type;

    private String name;

    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer status;

    private String additionalInfo;

    private byte[] logo;

    private String logoType;


}

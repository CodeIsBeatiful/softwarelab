package com.blackstar.softwarelab.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
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

    @TableId
    private String name;

    private String author;

    private String type;

    private String description;

    private String additionalInfo;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer status;

}

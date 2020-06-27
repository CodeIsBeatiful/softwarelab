package com.blackstar.softwarelab.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author blackstar
 * @since 2020-06-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AppVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private String appName;

    @TableId
    private String version;

    private String additionalInfo;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer status;


}

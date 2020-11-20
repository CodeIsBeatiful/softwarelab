package com.softwarelab.application.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author blackstar
 * @since 2020-10-25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AppExtension implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private String appName;

    private Integer usedCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer status;
}

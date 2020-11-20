package com.softwarelab.application.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author blackstar
 * @since 2020-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AppSource  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String version;

    private String repository;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer status;
}

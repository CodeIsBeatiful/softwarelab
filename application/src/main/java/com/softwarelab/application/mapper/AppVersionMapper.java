package com.softwarelab.application.mapper;

import com.softwarelab.application.entity.AppVersion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author blackstar
 * @since 2020-06-27
 */
public interface AppVersionMapper extends BaseMapper<AppVersion> {

    @Select({"<script>",
            "select version,download_status downloadStatus from app_version",
            " where app_name = #{appName}",
            "</script>"})
    List<AppVersion> getSimpleByAppName(String appName);
}

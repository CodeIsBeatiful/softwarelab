package com.blackstar.softwarelab.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blackstar.softwarelab.entity.App;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author blackstar
 * @since 2020-03-28
 */
public interface AppMapper extends BaseMapper<App> {


    @Select({"<script>",
            "select name from app",
            "<when test='type != null and type!=\"\"'>",
            "where type = #{type}",
            "</when>",
            "</script>"})
    List<String> getNameByType(@Param("type") String type);
}

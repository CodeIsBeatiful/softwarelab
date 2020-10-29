package com.blackstar.softwarelab.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blackstar.softwarelab.bean.AppInfo;
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


    @Select("select app.*,appExt.USED_COUNT from (\n" +
            "    select APP_NAME ,USED_COUNT from app_extension where used_count > 0 order by used_count desc\n" +
            "    ) appExt join APP app on appExt.APP_NAME = app.NAME limit #{limit}\n")
    List<AppInfo> getTop(int topNumber);
}

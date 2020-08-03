package com.blackstar.softwarelab.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@Configuration
@MapperScan("com.blackstar.softwarelab.mapper")
public class MybatisPlusConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        //Set the requested page is greater than the maximum page operation
        // true to return to the homepage, false to continue the request, the default is false
        // paginationInterceptor.setOverflow(false);
        // Set the maximum single page limit number,
        // the default is 500, -1 is not limited
        // paginationInterceptor.setLimit(500);
        // Enable count join optimization, only for part of the left join
        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
        return paginationInterceptor;
    }
}
package com.blackstar.softwarelab.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
@Slf4j
public class DataInitConfig {

    private final String INIT_DATA_SCRIPT = "classpath:db/data.sql";

    @Autowired
    private DataSource datasource;

    @PostConstruct
    public void init() throws Exception {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(INIT_DATA_SCRIPT);
        Connection connection = datasource.getConnection();
        if (!dataIsExist(connection)) {
            log.info("begin init data from {}", INIT_DATA_SCRIPT);
            ScriptUtils.executeSqlScript(connection, resource);
        }

    }

    private boolean dataIsExist(Connection connection) {

        boolean flag = false;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select count(1) from sys_user");
            while (resultSet.next()) {
                flag = resultSet.getInt(1) > 0;
            }

            return flag;
        } catch (SQLException e) {
            log.error("data init failed", e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

}

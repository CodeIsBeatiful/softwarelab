package com.blackstar.softwarelab.config;

import com.blackstar.softwarelab.service.IAppSourceService;
import com.blackstar.softwarelab.util.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
@Slf4j
//todo rewrite: check table app_version
public class DataInitConfig {

    private final String INIT_SOURCE_FILE_NAME = "source.zip";

    @Autowired
    private DataSource datasource;

    @Autowired
    private IAppSourceService appSourceService;

    @PostConstruct
    public void init() throws Exception {
        Connection connection = datasource.getConnection();
        if (!dataIsExist(connection)) {
            log.info("begin init data from {}", INIT_SOURCE_FILE_NAME);
            //todo check package exists？
            String dataDir = System.getProperty("data.path");
            File file = new File(dataDir + File.separator + INIT_SOURCE_FILE_NAME);
            if (file.exists()) {
                //unzip to source package
                String targetDir = dataDir+File.separator+"source";
                ZipUtil.unZip(file.getPath(), targetDir, 1);
                //load to db
                appSourceService.loadToDb();
                log.info("data init success: {}",INIT_SOURCE_FILE_NAME);
            }else {
                log.warn("can't find source: {}",INIT_SOURCE_FILE_NAME);
            }
        }

    }

    private boolean dataIsExist(Connection connection) {
        boolean flag = false;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select count(1) from app_version");
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
                    log.error("check data exist error", e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    log.error("check data exist error", e);
                }
            }
        }
        return flag;
    }

}

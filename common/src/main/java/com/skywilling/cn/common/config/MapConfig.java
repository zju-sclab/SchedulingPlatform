package com.skywilling.cn.common.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;


@Configuration
@ConfigurationProperties(prefix = "map")
public class MapConfig {
    @Value("${map.park.name.init}")
    private String initMap;

    @Value("${map.park.pathfile.init}")
    private String initPathFile;

    public String getInitMap(){
        return initMap;
    }

    public String getInitPathFile(){
        return initPathFile;
    }


}

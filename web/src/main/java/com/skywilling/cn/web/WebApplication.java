package com.skywilling.cn.web;

import com.skywilling.cn.livemap.service.impl.MapServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(scanBasePackages = {"com.skywilling.cn"})
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@MapperScan("com.skywilling.cn.*.*.mapper")
@EnableScheduling
@EnableAsync
public class WebApplication implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(WebApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

    @Override
    public void run(String... args) {
        LOG.info("SPRING BOOT INIT AT " + System.currentTimeMillis());
        LOG.debug("this is a debug");
        LOG.warn("this is a warn");
        LOG.error("this is a error");
    }
}

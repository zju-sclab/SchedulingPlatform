package com.skywilling.cn.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(scanBasePackages = {"com.skywilling.cn"})
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@MapperScan("com.skywilling.cn.*.*.mapper")
public class WebApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println(System.currentTimeMillis());
    }
}

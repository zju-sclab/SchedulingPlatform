package com.skywilling.cn.web;


import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;


@SpringBootApplication(scanBasePackages = {"com.skywilling.cn"}, exclude = {DataSourceAutoConfiguration.class, MongoAutoConfiguration.class})
@MapperScan("com.skywilling.cn.*.*.mapper")
public class WebApplication implements CommandLineRunner {

    public static void main(String[] args) {

        SpringApplication.run(WebApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}

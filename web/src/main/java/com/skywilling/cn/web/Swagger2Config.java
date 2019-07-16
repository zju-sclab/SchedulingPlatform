package com.skywilling.cn.web;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by Trace on 2018-05-16.<br/>
 * Desc: swagger2配置类
 */

//通过注解@EnableSwagger2开启swagger2，apiInfo是接口文档的基本说明信息，包括标题、描述、服务网址、联系人、版本等信息
@SuppressWarnings({"unused"})
@Configuration @EnableSwagger2
public class Swagger2Config {
    //@Value("${swagger2.enable}")
    private boolean enable = true;

    @Bean("AutoApis")
    public Docket AutoApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("自动驾驶模块")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.regex("/api/v2/auto.*"))
                .build()
                .apiInfo(apiInfo())
                .enable(enable);
    }

    @Bean("CarInfoApis")
    public Docket CarInfoApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("车辆信息模块")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.regex("/api/v2/info.*"))
                .build()
                .apiInfo(apiInfo())
                .enable(enable);
    }

    @Bean("OrderApis")
    public Docket OrderApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("订单管理模块")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.regex("/api/v2/order.*"))
                .build()
                .apiInfo(apiInfo())
                .enable(enable);
    }

    @Bean("ParkApis")
    public Docket ParkApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("园区管理模块")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.regex("/api/v2/park.*"))
                .build()
                .apiInfo(apiInfo())
                .enable(enable);
    }

    @Bean("TaskApis")
    public Docket TaskApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("任务管理模块")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.regex("/api/v2/task.*"))
                .build()
                .apiInfo(apiInfo())
                .enable(enable);
    }

    @Bean("UserApis")
    public Docket UserApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("用户管理模块")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.regex("/api/v2/user.*"))
                .build()
                .apiInfo(apiInfo())
                .enable(enable);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("SchedulePlatform系统平台接口文档")
                .description("目前提供自动驾驶的文档")
                .version("1.0")
                .build();
    }
}
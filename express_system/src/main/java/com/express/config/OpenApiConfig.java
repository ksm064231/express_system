package com.express.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("快递代收管理系统 API")
                        .version("1.0.0")
                        .description("快递代收管理系统后端接口文档\n\n" +
                                "为小区、学校或办公楼等场所提供快递入库、取件、退件及逾期提醒服务。")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@express.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}

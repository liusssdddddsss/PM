package com.example.springboot.common;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.webmvc.api.OpenApiActuatorResource;

public class SwaggerConfig {
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Boot 4 API接口文档")
                        .version("1.0。0")
                        .description("本文档涵盖了所有模块的接口说明")
                        .contact(new Contact().name("").url("https://javaxmsz.cn"))
                );
    }
}

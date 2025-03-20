package com.ssilvadev.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("REST API's RESTful form 0 with Java, Spring Boot, Kubernetes and Docker")
                        .version("v1")
                        .description("REST API's RESTful form 0 with Java, Spring Boot, Kubernetes and Docker")
                        .termsOfService("https://www.ssilvadev.com/")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.ssilvadev.com/")));
    }
}

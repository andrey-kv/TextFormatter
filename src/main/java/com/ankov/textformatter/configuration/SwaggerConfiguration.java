package com.ankov.textformatter.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@EnableSwagger2
@Configuration
public class SwaggerConfiguration {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ankov.textformatter"))
                .build()
                .apiInfo(apiDetails());
    }

    private ApiInfo apiDetails() {

        var myContact = new springfox.documentation.service.Contact(
                "Andrii Kovalenko",
                "mywebsite.com.ua",
                "a.kovalenko.ua@gmail.com");

        return new ApiInfo(
                "TextFormatter API",
                "Sample application with REST API. Use Spring Boot, PostgreSQL, OpenAPI",
                "1.0",
                "(c) 2021",
                myContact,
                "API License",
                "LicenseUrl.com",
                Collections.emptyList());
    }
}

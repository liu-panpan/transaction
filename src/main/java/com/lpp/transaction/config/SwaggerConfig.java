package com.lpp.transaction.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig  {

    @Bean
    public Docket petApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.lpp.transaction.controller"))
                .paths(PathSelectors.any()) 
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Transaction Api Documentation").description("Transaction Api Documentation")
                .contact(new Contact("panpan.liu", "", "panpan.liu@cienet.com.cn")).version("1.0").build();
    }
    
}
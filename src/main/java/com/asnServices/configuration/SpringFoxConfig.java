package com.asnServices.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringFoxConfig {

        @Bean
        public Docket api() {
            return new Docket(DocumentationType.SWAGGER_2)
                    .select()
                    .apis(RequestHandlerSelectors.basePackage("com.operationalService.controller"))
                    .paths(PathSelectors.any())
                    .build()
                    .apiInfo(apiInfo())
                    .useDefaultResponseMessages(false);// Disable default response messages

        }

        private ApiInfo apiInfo() {
            return new ApiInfoBuilder()
                    .title("Operation services")
                    .description("Api For Operation services ")
                    .version("1.0.0")
                    .build();
        }
}
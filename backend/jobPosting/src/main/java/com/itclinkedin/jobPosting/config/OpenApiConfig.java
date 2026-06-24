package com.itclinkedin.jobPosting.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LinkedIn Clone - Job Posting Microservice API")
                        .version("1.0.0")
                        .description("High-throughput 5NF Shard Distributed REST Layer for Job Lifecycle Tracking.")
                        .contact(new Contact().name("ITC LinkedIn Architecture Team")));
    }
}
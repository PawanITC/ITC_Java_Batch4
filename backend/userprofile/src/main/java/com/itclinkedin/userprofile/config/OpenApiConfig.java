package com.itclinkedin.userprofile.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userProfileOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LinkedIn Clone - User Profile Service API")
                        .version("1.0.0")
                        .description("API Documentation for managing user profiles, skills, experiences, Education, Language and  followers infrastructure.")
                        .contact(new Contact()
                                .name("Backend Team")
                                .email("hasnainahmad4890@gmail.com")));
    }
}
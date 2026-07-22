package com.itc.linkedin.searchAndDiscover.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI searchDiscoveryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Search and Discovery Service API")
                        .version("1.0")
                        .description("APIs for search, discovery suggestions and trending topics"))
                .addSecurityItem(new SecurityRequirement().addList("keycloakOAuth2"))
                .components(new Components()
                        .addSecuritySchemes("keycloakOAuth2",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .flows(new OAuthFlows()
                                                .authorizationCode(new OAuthFlow()
                                                        .authorizationUrl("http://localhost:8080/realms/linkedin-app/protocol/openid-connect/auth")
                                                        .tokenUrl("http://localhost:8080/realms/linkedin-app/protocol/openid-connect/token")
                                                        .scopes(new Scopes()
                                                                .addString("openid", "OpenID scope")
                                                                .addString("profile", "Profile scope")
                                                                .addString("email", "Email scope")
                                                        )
                                                )
                                        )
                        )
                );
    }
}
package com.itc.linkedin.connections_service.config;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Connection Service API",
                version = "v1",
                description = "LinkedIn Clone Connection Service APIs for managing connection requests, accepted connections, and relationship status.",
                contact = @Contact(
                        name = "LinkedIn Clone Team"
                )
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT token issued by Keycloak and forwarded through API Gateway"
)
public class OpenApiConfig {
}

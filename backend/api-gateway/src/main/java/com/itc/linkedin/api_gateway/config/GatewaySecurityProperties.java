package com.itc.linkedin.api_gateway.config;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Component
@Validated
@ConfigurationProperties(prefix = "app.gateway.security")
public class GatewaySecurityProperties {

    @NotEmpty
    private List<String> allowedOrigins = List.of("http://localhost:3000");

    @NotEmpty
    private List<String> allowedTokenClients = List.of("linkedin-frontend", "api-gateway");

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public List<String> getAllowedTokenClients() {
        return allowedTokenClients;
    }

    public void setAllowedTokenClients(List<String> allowedTokenClients) {
        this.allowedTokenClients = allowedTokenClients;
    }
}

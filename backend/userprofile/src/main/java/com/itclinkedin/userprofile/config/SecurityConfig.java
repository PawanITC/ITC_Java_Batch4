package com.itclinkedin.userprofile.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
<<<<<<< Updated upstream
                        .anyRequest().permitAll()
=======
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated()
>>>>>>> Stashed changes
                );

        return http.build();
    }
}
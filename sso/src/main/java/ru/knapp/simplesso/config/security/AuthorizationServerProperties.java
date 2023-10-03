package ru.knapp.simplesso.config.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.authorizationserver")
public class AuthorizationServerProperties {
    private String issuerUrl;
    private String introspectionEndpoint;
}

package ru.knapp.simplesso.resourceservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import ru.knapp.simplesso.resourceservice.config.introspector.CustomSpringTokenIntrospection;

@EnableMethodSecurity
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class ResourceServerConfig {

    private final OAuth2ResourceOpaqueProperties resourceProperties;
    private final MappingJackson2HttpMessageConverter messageConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(customizer -> customizer.anyRequest().authenticated());

        http.oauth2ResourceServer(configurer -> {
            configurer.opaqueToken(customizer -> {
                customizer.introspector(new CustomSpringTokenIntrospection(
                    resourceProperties.getIntrospectionUri(),
                    resourceProperties.getClientId(),
                    resourceProperties.getClientSecret(),
                    messageConverter
                ));
            });
        });
        return http.build();
    }
}

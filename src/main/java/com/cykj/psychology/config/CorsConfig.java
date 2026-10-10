package com.cykj.psychology.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    private final String[] allowedOriginPatterns;

    public CorsConfig(
            @Value("${app.cors.allowed-origin-patterns:"
                    + "http://localhost:*,http://127.0.0.1:*,"
                    + "http://192.168.*:*,http://10.*:*,http://172.*:*}")
            String allowedOriginPatterns) {
        this.allowedOriginPatterns = allowedOriginPatterns.split(",");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(allowedOriginPatterns)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type")
                .allowCredentials(true)
                .maxAge(3600);
    }
}

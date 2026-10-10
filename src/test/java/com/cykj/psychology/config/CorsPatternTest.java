package com.cykj.psychology.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CorsPatternTest {

    @Test
    void acceptsLocalhostAndPrivateLanDevelopmentOrigins() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://192.168.*:*"
        ));

        assertEquals(
                "http://localhost:8082",
                configuration.checkOrigin("http://localhost:8082")
        );
        assertEquals(
                "http://192.168.2.112:8082",
                configuration.checkOrigin("http://192.168.2.112:8082")
        );
        assertNull(configuration.checkOrigin("https://untrusted.example.com"));
    }
}

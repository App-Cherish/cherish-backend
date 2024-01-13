package com.cherish.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;

@Configuration
@RequiredArgsConstructor
public class BeanConfig {

    private final RestTemplateResponseErrorHandler responseErrorHandler;

    @Bean
    public Clock clock () {
        return Clock.systemDefaultZone();
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(responseErrorHandler);
        return restTemplate;
    }
}

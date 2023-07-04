package ru.lazarenko.studentmanager.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {
    private final RestTemplateCustomizer restTemplateCustomizer;

    @Bean
    public RestTemplateCustomizer restTemplateCustomizer() {
        return new BasicAuthRestTemplateCustomizer();
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplateCustomizer.customize(restTemplate);
        return restTemplate;
    }
}


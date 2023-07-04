package ru.lazarenko.paymentservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Component
public class BasicAuthRestTemplateCustomizer implements RestTemplateCustomizer {
    @Value("${admin.login}")
    private String username;
    @Value("${admin.password}")
    private String password;

    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.getInterceptors().add(getBasicAuthInterceptor());
    }

    private ClientHttpRequestInterceptor getBasicAuthInterceptor() {
        return (request, body, execution) -> {
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
            String authHeader = "Basic " + new String(encodedAuth);
            request.getHeaders().add(HttpHeaders.AUTHORIZATION, authHeader);
            return execution.execute(request, body);
        };
    }
}
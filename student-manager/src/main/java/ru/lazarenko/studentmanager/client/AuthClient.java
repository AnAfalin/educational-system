package ru.lazarenko.studentmanager.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.lazarenko.securitymanager.dto.GenerateTokenRequest;
import ru.lazarenko.securitymanager.dto.TokenDto;
import ru.lazarenko.securitymanager.dto.UserSecurityInfo;

@Component
@RequiredArgsConstructor
public class AuthClient {
    private final static String POST_TOKEN_VALIDATE = "/api/tokens/validate";
    private final static String POST_TOKEN_GENERATE = "/api/tokens/generate";
    private final RestTemplate restTemplate;

    @Value("${address.user-manager}")
    private String authManagerAddress;

    public UserSecurityInfo validateToken(String token) {
        String url = authManagerAddress.concat(POST_TOKEN_VALIDATE);
        return restTemplate.postForObject(url, token, UserSecurityInfo.class);
    }

    public TokenDto generateToken(GenerateTokenRequest request) {
        String url = authManagerAddress.concat(POST_TOKEN_GENERATE);
        return restTemplate.postForObject(url, request, TokenDto.class);
    }
}

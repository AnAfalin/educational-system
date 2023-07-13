package ru.lazarenko.coursemanager.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.lazarenko.securitymanager.dto.UserDetailsDto;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final static String POST_USER_DETAILS = "/api/users/details";
    private final RestTemplate restTemplate;

    @Value("${address.user-manager}")
    private String userManagerAddress;

    public UserDetailsDto getByUsername(String username) {
        String url = userManagerAddress.concat(POST_USER_DETAILS);
        return restTemplate.postForObject(url, username, UserDetailsDto.class);
    }
}
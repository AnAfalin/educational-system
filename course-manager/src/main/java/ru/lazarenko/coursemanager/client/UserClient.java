package ru.lazarenko.coursemanager.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.lazarenko.model.dto.security.UserDetailsDto;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final static String GET_USER_DETAILS_ENDPOINT = "/api/users/details/{username}";
    private final RestTemplate restTemplate;

    @Value("${address.user-manager}")
    private String userManagerAddress;

    public UserDetailsDto getByUsername(String username) {
        String url = userManagerAddress.concat(GET_USER_DETAILS_ENDPOINT);
        return restTemplate.getForObject(url, UserDetailsDto.class, username);
    }
}
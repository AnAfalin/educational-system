package ru.lazarenko.studentmanager.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.lazarenko.studentmanager.dto.UserDetailsDto;
import ru.lazarenko.studentmanager.dto.UserRegisterRequest;
import ru.lazarenko.studentmanager.dto.UserRegisterResponse;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final static String GET_USER_DETAILS_ENDPOINT = "/api/users/details/{username}";
    private final static String POST_USER_ENDPOINT = "/api/users/reg";

    @Value("${address.user-manager}")
    private String userManagerAddress;

    private final RestTemplate restTemplate;

    public UserDetailsDto getByUsername(String username) {
        String url = userManagerAddress.concat(GET_USER_DETAILS_ENDPOINT);

        return restTemplate.getForObject(url, UserDetailsDto.class, username);
    }

    public UserRegisterResponse createNewUser(UserRegisterRequest request) {
        String url = userManagerAddress.concat(POST_USER_ENDPOINT);

        return restTemplate.postForObject(url, request, UserRegisterResponse.class);
    }
}

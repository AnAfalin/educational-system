package ru.lazarenko.studentmanager.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.lazarenko.model.dto.register.UserRegisterRequest;
import ru.lazarenko.model.dto.register.UserRegisterResponse;
import ru.lazarenko.model.dto.security.UserDetailsDto;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final static String POST_USER_DETAILS = "/api/users/details";
    private final static String REG_NEW_USER = "/api/users/reg";
    private final static String CHECK_EXIST_USERNAME = "/api/users/check-exist/{username}";

    @Value("${address.user-manager}")
    private String userManagerAddress;

    private final RestTemplate restTemplate;

    public UserDetailsDto getByUsername(String username) {
        String url = userManagerAddress.concat(POST_USER_DETAILS);
        return restTemplate.postForObject(url, username, UserDetailsDto.class);
    }

    public UserRegisterResponse createNewUser(UserRegisterRequest request) {
        String url = userManagerAddress.concat(REG_NEW_USER);

        return restTemplate.postForObject(url, request, UserRegisterResponse.class);
    }

    public Boolean checkUniqueUsername(String username) {
        String url = userManagerAddress.concat(CHECK_EXIST_USERNAME);

        return restTemplate.getForObject(url, Boolean.class, username);
    }
}

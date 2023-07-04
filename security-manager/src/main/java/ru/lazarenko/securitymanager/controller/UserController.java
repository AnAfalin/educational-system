package ru.lazarenko.securitymanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.lazarenko.securitymanager.dto.UserDetailsDto;
import ru.lazarenko.securitymanager.dto.UserRegisterRequest;
import ru.lazarenko.securitymanager.dto.UserRegisterResponse;
import ru.lazarenko.securitymanager.security.CustomUserDetailsService;
import ru.lazarenko.securitymanager.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final CustomUserDetailsService customUserDetailsService;
    private final UserService userService;

    @PostMapping("/reg")
    public UserRegisterResponse createUser(@Valid @RequestBody UserRegisterRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/details/{username}")
    public UserDetailsDto getUserDetails(@PathVariable String username) {
        UserDetails loadedUser = customUserDetailsService.loadUserByUsername(username);

        return UserDetailsDto.getUserDetailsDto(loadedUser);
    }

    @GetMapping("/users")
    public List<UserRegisterResponse> getAllUsers() {
        return userService.getAllUsers();
    }
}

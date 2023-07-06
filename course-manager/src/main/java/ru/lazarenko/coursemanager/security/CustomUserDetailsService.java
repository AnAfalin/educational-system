package ru.lazarenko.coursemanager.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.lazarenko.coursemanager.client.UserClient;
import ru.lazarenko.coursemanager.dto.UserDetailsDto;
import ru.lazarenko.coursemanager.model.Role;
import ru.lazarenko.coursemanager.model.User;
import ru.lazarenko.coursemanager.model.UserRole;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserDetailsDto userDetailsDto = userClient.getByUsername(username);

        return new SecurityUser(new User(userDetailsDto.getUsername(),
                userDetailsDto.getPassword(),
                userDetailsDto.getRoles()
                        .stream()
                        .map(roleDto -> new Role(UserRole.valueOf(roleDto.getName())))
                        .collect(Collectors.toList())));
    }
}
package ru.lazarenko.studentmanager.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.lazarenko.studentmanager.client.UserClient;
import ru.lazarenko.studentmanager.dto.UserDetailsDto;
import ru.lazarenko.studentmanager.model.Role;
import ru.lazarenko.studentmanager.model.User;
import ru.lazarenko.studentmanager.model.UserRole;

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

package ru.lazarenko.securitymanager.dto;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDto {
    private String username;

    private String password;

    private List<RoleDto> roles = new ArrayList<>();

    public static UserDetailsDto getUserDetailsDto(UserDetails loadedUser) {
        return UserDetailsDto.builder()
                .username(loadedUser.getUsername())
                .password(loadedUser.getPassword())
                .roles(loadedUser.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(str -> str.replace("ROLE_", ""))
                        .map(RoleDto::new)
                        .collect(Collectors.toList()))
                .build();
    }
}

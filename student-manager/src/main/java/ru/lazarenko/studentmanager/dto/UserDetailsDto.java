package ru.lazarenko.studentmanager.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDto {
    private String username;

    private String password;

    private List<RoleDto> roles = new ArrayList<>();
}

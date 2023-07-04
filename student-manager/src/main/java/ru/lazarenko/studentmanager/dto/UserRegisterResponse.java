package ru.lazarenko.studentmanager.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterResponse {
    private Integer id;

    private String username;

    private LocalDate registrationDate;

    private List<RoleDto> roles = new ArrayList<>();
}

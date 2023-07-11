package ru.lazarenko.model.dto.register;

import lombok.*;
import ru.lazarenko.model.dto.security.RoleDto;

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

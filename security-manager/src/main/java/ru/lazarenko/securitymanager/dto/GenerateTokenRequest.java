package ru.lazarenko.securitymanager.dto;

import lombok.*;
import ru.lazarenko.securitymanager.model.TypeToken;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenerateTokenRequest {
    private String username;
    private List<RoleDto> authorities;
    private TypeToken typeToken;
}

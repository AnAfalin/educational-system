package ru.lazarenko.model.dto.login;

import lombok.*;
import ru.lazarenko.model.dto.security.TokenDto;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponse {
    private String email;
    private List<String> roles;
    private TokenDto accessToken;
    private TokenDto refreshToken;
}

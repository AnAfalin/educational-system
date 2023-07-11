package ru.lazarenko.model.dto.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenResponse {
    private String username;
    private TokenDto accessToken;
    private TokenDto refreshToken;
}

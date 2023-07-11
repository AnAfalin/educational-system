package ru.lazarenko.model.dto.security;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
    private String token;
    private LocalDateTime expirationDataTime;
}

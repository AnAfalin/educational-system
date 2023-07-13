package ru.lazarenko.securitymanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.lazarenko.model.model.UserRole;
import ru.lazarenko.securitymanager.dto.GenerateTokenRequest;
import ru.lazarenko.securitymanager.dto.TokenDto;
import ru.lazarenko.securitymanager.dto.UserSecurityInfo;
import ru.lazarenko.securitymanager.entity.Role;
import ru.lazarenko.securitymanager.security.JwtService;
import ru.lazarenko.securitymanager.security.SecurityRole;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
public class TokenController {
    private final JwtService jwtService;

    @PostMapping("/validate")
    public UserSecurityInfo validateToken(@RequestBody String token) {
        return jwtService.validateToken(token);
    }

    @PostMapping("/generate")
    public TokenDto generateToken(@RequestBody GenerateTokenRequest request) {
        return jwtService.generateToken(request.getUsername(),
                request.getAuthorities()
                        .stream()
                        .map(el -> {
                            Role role = new Role();
                            role.setName(UserRole.valueOf(el.getName()));
                            return new SecurityRole(role);
                        })
                        .collect(Collectors.toList()),
                request.getTypeToken());
    }
}

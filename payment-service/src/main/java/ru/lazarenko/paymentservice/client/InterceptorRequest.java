package ru.lazarenko.paymentservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.lazarenko.securitymanager.dto.GenerateTokenRequest;
import ru.lazarenko.securitymanager.dto.RoleDto;
import ru.lazarenko.securitymanager.dto.TokenDto;
import ru.lazarenko.securitymanager.model.TypeToken;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class InterceptorRequest {
    private final AuthClient authClient;

    public void interceptRequest(RestTemplate restTemplate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        TokenDto tokenDto = authClient.generateToken(GenerateTokenRequest.builder()
                .username(authentication.getName())
                .authorities(authentication.getAuthorities()
                        .stream()
                        .map(el -> new RoleDto(el.getAuthority().replace("ROLE_", "")))
                        .collect(Collectors.toList()))
                .typeToken(TypeToken.ACCESS)
                .build());

        restTemplate.setInterceptors(List.of((request, body, execution) -> {
            request.getHeaders().add(HttpHeaders.AUTHORIZATION, tokenDto.getToken());
            return execution.execute(request, body);
        }));
    }
}

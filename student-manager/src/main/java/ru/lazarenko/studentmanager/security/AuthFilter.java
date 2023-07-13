package ru.lazarenko.studentmanager.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.lazarenko.model.dto.ResponseDto;
import ru.lazarenko.model.dto.login.UserLoginRequest;
import ru.lazarenko.securitymanager.dto.GenerateTokenRequest;
import ru.lazarenko.securitymanager.dto.RoleDto;
import ru.lazarenko.securitymanager.dto.TokenDto;
import ru.lazarenko.securitymanager.dto.UserTokenResponse;
import ru.lazarenko.securitymanager.model.TypeToken;
import ru.lazarenko.studentmanager.client.AuthClient;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AuthFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final AuthClient authClient;
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        if (!"POST".equals(request.getMethod())) {
            throw new AuthenticationServiceException("Request should be POST");
        }

        UserLoginRequest userLoginRequest = objectMapper.readValue(request.getInputStream(), UserLoginRequest.class);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userLoginRequest.getUsername(), userLoginRequest.getPassword());

        return authenticationManager.authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        TokenDto newAccessToken = authClient.generateToken(GenerateTokenRequest.builder()
                .username(authResult.getName())
                .authorities(authResult.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(str -> str.replace("ROLE_", ""))
                        .map(RoleDto::new)
                        .collect(Collectors.toList()))
                .typeToken(TypeToken.ACCESS)
                .build());
        TokenDto newRefreshToken = authClient.generateToken(GenerateTokenRequest.builder()
                .username(authResult.getName())
                .authorities(authResult.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(str -> str.replace("ROLE_", ""))
                        .map(RoleDto::new)
                        .collect(Collectors.toList()))
                .typeToken(TypeToken.REFRESH)
                .build());

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getOutputStream(),
                new UserTokenResponse(authResult.getName(), newAccessToken, newRefreshToken));

        SecurityContextHolder.getContext().setAuthentication(authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getOutputStream(),
                new ResponseDto(HttpStatus.UNAUTHORIZED.toString(), "Password or login is incorrect"));
    }
}

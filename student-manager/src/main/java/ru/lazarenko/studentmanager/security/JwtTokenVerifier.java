package ru.lazarenko.studentmanager.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.lazarenko.securitymanager.config.JwtConfig;
import ru.lazarenko.securitymanager.dto.*;
import ru.lazarenko.securitymanager.model.TypeToken;
import ru.lazarenko.studentmanager.client.AuthClient;
import ru.lazarenko.studentmanager.config.SecurityConfig;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtTokenVerifier extends OncePerRequestFilter {
    private final AuthClient authClient;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        String path = request.getRequestURI();

        if (SecurityConfig.whiteListUrls.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (path.contains("/api/refresh-token")) {

            if (!"POST".equals(request.getMethod())) {
                throw new AuthenticationServiceException("Request should be POST");
            }

            RefreshToken refreshToken = objectMapper.readValue(request.getInputStream(), RefreshToken.class);
            UserSecurityInfo userSecurityInfo = authClient.validateToken(refreshToken.getToken());

            TokenDto newAccessToken = authClient.generateToken(GenerateTokenRequest.builder()
                    .username(userSecurityInfo.getUsername())
                    .authorities(userSecurityInfo.getAuthorities())
                    .typeToken(TypeToken.ACCESS)
                    .build());
            TokenDto newRefreshToken = authClient.generateToken(GenerateTokenRequest.builder()
                    .username(userSecurityInfo.getUsername())
                    .authorities(userSecurityInfo.getAuthorities())
                    .typeToken(TypeToken.REFRESH)
                    .build());

            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            objectMapper.writeValue(response.getOutputStream(),
                    new UserTokenResponse(userSecurityInfo.getUsername(), newAccessToken, newRefreshToken));

            filterChain.doFilter(request, response);

            return;
        }

        if (path.contains("/login") && "POST".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader(JwtConfig.HEADER);

        if (authorizationHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = authorizationHeader.replace(JwtConfig.TOKEN_PREFIX, "");
        UserSecurityInfo userSecurityInfo = authClient.validateToken(accessToken);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userSecurityInfo.getUsername(), null,
                userSecurityInfo.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

}

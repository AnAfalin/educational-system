package ru.lazarenko.securitymanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import ru.lazarenko.securitymanager.config.JwtConfig;
import ru.lazarenko.securitymanager.dto.RoleDto;
import ru.lazarenko.securitymanager.dto.TokenDto;
import ru.lazarenko.securitymanager.dto.UserSecurityInfo;
import ru.lazarenko.securitymanager.model.TypeToken;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    public TokenDto generateToken(String username, Collection<? extends GrantedAuthority> authorities,
                                  TypeToken typeToken) {

        int expirationTime = (typeToken == TypeToken.ACCESS
                ? jwtConfig.getAccessTokenExpirationMs()
                : jwtConfig.getRefreshTokenExpirationMs());

        Date expirationDate = new Date(new Date().getTime() + expirationTime);
        LocalDateTime expirationDateTime = expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        String token = Jwts.builder()
                .setIssuer("security-application")
                .setSubject(username)
                .claim("authorities", authorities)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return TokenDto.builder()
                .token(JwtConfig.TOKEN_PREFIX.concat(token))
                .expirationDataTime(expirationDateTime)
                .build();
    }

    public UserSecurityInfo validateToken(String token) {

        token = token.replace(JwtConfig.TOKEN_PREFIX, "");

        Claims payload = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String username = payload.getSubject();

        Collection<? extends GrantedAuthority> grantedAuthorities = obtainAuthorities(payload);

        return new UserSecurityInfo(username, grantedAuthorities.stream().map(el -> new RoleDto(el.getAuthority())).collect(Collectors.toList()));
    }

    private Collection<? extends GrantedAuthority> obtainAuthorities(Claims payload) {
        List<Map<String, String>> authorities = payload.get("authorities", List.class);
        return authorities
                .stream()
                .map(auth -> new SimpleGrantedAuthority(auth.get("authority")))
                .toList();
    }
}

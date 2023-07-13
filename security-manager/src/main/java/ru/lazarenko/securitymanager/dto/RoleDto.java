package ru.lazarenko.securitymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto implements GrantedAuthority {
    private String name;

    @Override
    public String getAuthority() {
        return name;
    }
}

package ru.lazarenko.coursemanager.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import ru.lazarenko.coursemanager.model.Role;

@RequiredArgsConstructor
public class SecurityRole implements GrantedAuthority {

    private final Role role;

    @Override
    public String getAuthority() {
        String prefix = "ROLE_";
        String name = role.getName().name().toUpperCase();
        if (!name.startsWith(prefix)) {
            name = prefix.concat(name);
        }
        return name;
    }
}

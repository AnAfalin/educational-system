package ru.lazarenko.securitymanager.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.lazarenko.model.model.UserRole;
import ru.lazarenko.securitymanager.entity.Role;
import ru.lazarenko.securitymanager.entity.User;
import ru.lazarenko.securitymanager.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class CustomUserDetailsServiceTest {
    @Autowired
    CustomUserDetailsService underTest;

    @MockBean
    UserRepository userRepository;

    @Test
    @DisplayName("load user by username | UsernameNotFoundException | user does not exist")
    void loadUserByUsername_usernameNotFoundException_userDoesNotExist() {
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> underTest.loadUserByUsername("unknown"));

        verify(userRepository, times(1)).findByUsername(anyString());

    }

    @Test
    @DisplayName("load user by username | correct returned object | user exist")
    void loadUserByUsername_correctReturnedObject_userExist() {
        Role role = new Role();
        role.setName(UserRole.ADMIN);
        User user = User.builder()
                .id(1)
                .registrationDate(LocalDate.now())
                .username("Mike")
                .password("8wrWubUB&%@5K")
                .roles(List.of(role))
                .build();

        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(user));

        UserDetails result = underTest.loadUserByUsername("Mike");

        assertThat(result.getUsername()).isEqualTo("Mike");
        assertThat(result.getPassword()).isEqualTo("8wrWubUB&%@5K");
        assertThat(result.getAuthorities().size()).isEqualTo(1);

        verify(userRepository, times(1)).findByUsername(anyString());
    }

}
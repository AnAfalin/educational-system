package ru.lazarenko.studentmanager.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.lazarenko.model.dto.security.RoleDto;
import ru.lazarenko.model.dto.security.UserDetailsDto;
import ru.lazarenko.studentmanager.client.UserClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class CustomUserDetailsServiceTest {
    @Autowired
    CustomUserDetailsService underTest;

    @MockBean
    UserClient userClient;

    @Test
    @DisplayName("load user by username | UsernameNotFoundException | user does not exist")
    void loadUserByUsername_usernameNotFoundException_userDoesNotExist() {
        doThrow(UsernameNotFoundException.class)
                .when(userClient)
                .getByUsername(anyString());

        assertThrows(UsernameNotFoundException.class, () -> underTest.loadUserByUsername("unknown"));

        verify(userClient, times(1)).getByUsername(anyString());

    }

    @Test
    @DisplayName("load user by username | correct returned object | user exist")
    void loadUserByUsername_correctReturnedObject_userExist() {
        UserDetailsDto userDetailsDto = UserDetailsDto.builder()
                .username("Mike")
                .password("8wrWubUB&%@5K")
                .roles(List.of(new RoleDto("ADMIN")))
                .build();

        when(userClient.getByUsername(anyString()))
                .thenReturn(userDetailsDto);

        UserDetails result = underTest.loadUserByUsername("Mike");

        assertThat(result.getUsername()).isEqualTo("Mike");
        assertThat(result.getPassword()).isEqualTo("8wrWubUB&%@5K");
        assertThat(result.getAuthorities().size()).isEqualTo(1);

        verify(userClient, times(1)).getByUsername(anyString());
    }
}
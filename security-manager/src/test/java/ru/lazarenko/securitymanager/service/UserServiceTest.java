package ru.lazarenko.securitymanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.lazarenko.model.dto.register.UserRegisterRequest;
import ru.lazarenko.model.dto.register.UserRegisterResponse;
import ru.lazarenko.model.dto.security.RoleDto;
import ru.lazarenko.model.model.UserRole;
import ru.lazarenko.securitymanager.entity.Role;
import ru.lazarenko.securitymanager.entity.User;
import ru.lazarenko.securitymanager.repository.UserRepository;
import ru.lazarenko.securitymanager.service.mapper.UserMapper;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {
    @Autowired
    UserService underTest;

    @MockBean
    UserRepository userRepository;

    @MockBean
    UserMapper userMapper;

    @MockBean
    PasswordEncoder passwordEncoder;

    @Captor
    ArgumentCaptor<User> captor;

    UserRegisterRequest userRegisterRequest;
    UserRegisterResponse userRegisterResponse;
    User user;

    @BeforeEach
    void prepare() {
        userRegisterRequest = UserRegisterRequest.builder()
                .username("Mike")
                .password("mike2547")
                .build();
        userRegisterResponse = UserRegisterResponse.builder()
                .id(1)
                .registrationDate(LocalDate.now())
                .username("Mike")
                .roles(List.of(new RoleDto("ADMIN")))
                .build();

        Role role = new Role();
        role.setName(UserRole.ADMIN);
        user = User.builder()
                .id(1)
                .registrationDate(LocalDate.now())
                .username("Mike")
                .password("8wrWubUB&%@5K")
                .roles(List.of(role))
                .build();
    }

    @Test
    @DisplayName("create user | successfully create | request is correct")
    void createUser_successfullyCreate_requestIsCorrect() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        when(userMapper.toUserDto(any(User.class)))
                .thenReturn(userRegisterResponse);

        UserRegisterResponse result = underTest.createUser(userRegisterRequest);

        verify(userRepository, times(1)).save(any(User.class));

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getUsername()).isEqualTo("Mike");
    }

    @Test
    @DisplayName("get all users | empty result list | users do not exist")
    void getAllUsers_emptyResultList_usersDoNotExist() {
        when(userRepository.findAll())
                .thenReturn(List.of());

        when(userMapper.toListUserDto(anyList()))
                .thenReturn(List.of());

        List<UserRegisterResponse> result = underTest.getAllUsers();

        verify(userRepository, times(1)).findAll();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("get all users | not empty result list | users exist")
    void getAllUsers_notEmptyResultList_usersExist() {
        when(userRepository.findAll())
                .thenReturn(List.of(user));

        when(userMapper.toListUserDto(anyList()))
                .thenReturn(List.of(userRegisterResponse));

        List<UserRegisterResponse> result = underTest.getAllUsers();

        verify(userRepository, times(1)).findAll();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(0).getUsername()).isEqualTo("Mike");
    }

    @Test
    @DisplayName("check exist username | true | username exist")
    void checkExistUsername_true_usernameExist() {
        when(userRepository.checkExistUsername(anyString()))
                .thenReturn(1);

        Boolean result = underTest.checkExistUsername("Mike");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("check exist username | false | username does not exist")
    void checkExistUsername_true_usernameDoesNotExist() {
        when(userRepository.checkExistUsername(anyString()))
                .thenReturn(0);

        Boolean result = underTest.checkExistUsername("Mike");

        assertThat(result).isFalse();
    }
}
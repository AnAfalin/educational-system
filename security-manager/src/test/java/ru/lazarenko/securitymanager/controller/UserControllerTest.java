package ru.lazarenko.securitymanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.lazarenko.securitymanager.dto.RoleDto;
import ru.lazarenko.securitymanager.dto.UserDetailsDto;
import ru.lazarenko.securitymanager.dto.UserRegisterRequest;
import ru.lazarenko.securitymanager.dto.UserRegisterResponse;
import ru.lazarenko.securitymanager.entity.Role;
import ru.lazarenko.securitymanager.entity.User;
import ru.lazarenko.securitymanager.model.UserRole;
import ru.lazarenko.securitymanager.security.CustomUserDetailsService;
import ru.lazarenko.securitymanager.security.SecurityUser;
import ru.lazarenko.securitymanager.service.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration
class UserControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    UserService userService;

    @MockBean
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    ObjectMapper objectMapper;

    UserRegisterRequest userRegisterRequest;
    UserRegisterResponse userRegisterResponse;
    UserDetailsDto userDetailsDto;
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
        userDetailsDto = UserDetailsDto.builder()
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

    @Nested
    class ValidateUserTest {
        Validator validator;

        @BeforeEach
        void prepare() {
            try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
                validator = validatorFactory.getValidator();
            }
        }

        @Test
        @DisplayName("validate userRequest | size of validation list is 2 | all fields are incorrect")
        void validateUserRequest_correctSizeValidationList_allFieldsAreIncorrect() {
            UserRegisterRequest test = new UserRegisterRequest();

            List<ConstraintViolation<UserRegisterRequest>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(2, validationSet.size()),
                    () -> assertTrue(validationSet
                            .stream()
                            .map(ConstraintViolation::getMessage)
                            .toList()
                            .contains("Username cannot be empty or null"))
            );
        }

        @Test
        @DisplayName("validate userRequest | size of validation list is 1 | filed 'username' is null")
        void validateUserRequest_correctSizeValidationList_fieldUsernameIsNull() {
            UserRegisterRequest test = UserRegisterRequest.builder()
                    .password("password")
                    .build();

            List<ConstraintViolation<UserRegisterRequest>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Username cannot be empty or null", validationSet.get(0).getMessage()));
        }

        @Test
        @DisplayName("validate userRequest | size of validation list is 2 | filed 'username' is empty")
        void validateUserRequest_correctSizeValidationList_fieldUsernameIsEmpty() {
            UserRegisterRequest test = UserRegisterRequest.builder()
                    .username("")
                    .password("password")
                    .build();

            List<ConstraintViolation<UserRegisterRequest>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(2, validationSet.size()),
                    () -> assertTrue(validationSet
                            .stream()
                            .map(ConstraintViolation::getMessage)
                            .toList()
                            .contains("Username cannot be empty or null")));
        }

        @Test
        @DisplayName("validate userRequest | size of validation list is 1 | filed 'password' is null")
        void validateUserRequest_correctSizeValidationList_fieldPasswordIsNull() {
            UserRegisterRequest test = UserRegisterRequest.builder()
                    .username("Mike")
                    .build();

            List<ConstraintViolation<UserRegisterRequest>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(1, validationSet.size()),
                    () -> assertEquals("Password cannot be empty or null", validationSet.get(0).getMessage()));
        }

        @Test
        @DisplayName("validate userRequest | size of validation list is 1 | filed 'password' is empty")
        void validateUserRequest_correctSizeValidationList_fieldPasswordIsEmpty() {
            UserRegisterRequest test = UserRegisterRequest.builder()
                    .username("Mike")
                    .password("")
                    .build();

            List<ConstraintViolation<UserRegisterRequest>> validationSet = new ArrayList<>(validator.validate(test));
            assertAll(
                    () -> assertEquals(2, validationSet.size()),
                    () -> assertTrue(validationSet
                            .stream()
                            .map(ConstraintViolation::getMessage)
                            .toList()
                            .contains("Password must contains 5-15 characters (uppercase letters, lowercase letters or numbers)")));
        }
    }


    @Test
    @WithMockUser
    @DisplayName("create User | status is created | request is correct")
    void createUser_statusIsOk_requestIsCorrect() throws Exception {
        when(userService.createUser(any(UserRegisterRequest.class)))
                .thenReturn(userRegisterResponse);

        mvc.perform(post("/api/users/reg")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(userRegisterRequest)))
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.registrationDate")
                        .value(userRegisterResponse.getRegistrationDate().toString()));
    }

    @Test
    @WithMockUser
    @DisplayName("get user details| status is Ok | username is exist")
    void getUserDetails_statusIsOk_userExist() throws Exception {
        UserDetails userDetails = new SecurityUser(user);
        when(customUserDetailsService.loadUserByUsername(anyString()))
                .thenReturn(userDetails);

        mvc.perform(get("/api/users/details/Mike").with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Mike"))
                .andExpect(jsonPath("$.password").value("8wrWubUB&%@5K"))
                .andExpect(jsonPath("$.roles.size()").value(1))
                .andExpect(jsonPath("$.roles.[0].name").value("ADMIN"));
    }

    @Test
    @WithMockUser
    @DisplayName("get user details| status is NotFound | username is not exist")
    void getUserDetails_statusIsNotFound_userIsNotExist() throws Exception {
        doThrow(UsernameNotFoundException.class)
                .when(customUserDetailsService)
                .loadUserByUsername(anyString());

        mvc.perform(get("/api/users/details/unknown").with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.username").doesNotExist());
    }

    @Test
    @WithMockUser
    @DisplayName("get user details| empty result list | username is not exist")
    void getAllUsers_emptyResultList_usersAreNotExist() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of());

        mvc.perform(get("/api/users").with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$").isEmpty())
                .andExpect(jsonPath("$.[0].id").doesNotExist());
    }

    @Test
    @WithMockUser
    @DisplayName("get user details| not empty result list | username is not exist")
    void getAllUsers_notEmptyResultList_usersAreNotExist() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of(userRegisterResponse));

        mvc.perform(get("/api/users").with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.[0].id").exists())
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[0].username").value("Mike"));
    }

    @Test
    @WithMockUser
    @DisplayName("check exist username | true | username exist")
    void checkExistUsername_true_usernameExist() throws Exception {
        when(userService.checkExistUsername(anyString()))
                .thenReturn(true);

        mvc.perform(get("/api/users/check-exist/mike").with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    @WithMockUser
    @DisplayName("check exist username | true | username does not exist")
    void checkExistUsername_false_usernameExist() throws Exception {
        when(userService.checkExistUsername(anyString()))
                .thenReturn(false);

        mvc.perform(get("/api/users/check-exist/mike").with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$").value(false));
    }
}
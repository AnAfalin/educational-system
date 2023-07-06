package ru.lazarenko.studentmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.lazarenko.studentmanager.dto.ResponseDto;
import ru.lazarenko.studentmanager.dto.StudentRegisterRequest;
import ru.lazarenko.studentmanager.exception.NoUniqueObjectException;
import ru.lazarenko.studentmanager.service.StudentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    StudentService studentService;

    @Autowired
    ObjectMapper objectMapper;

    StudentRegisterRequest studentRegisterRequest;

    @BeforeEach
    void prepare() {
        studentRegisterRequest = StudentRegisterRequest.builder()
                .username("mike")
                .email("mike@mail.ru")
                .firstname("Михаил")
                .lastName("Смирнов")
                .patronymic("Дмитриевич")
                .password("password")
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("create new student | status is ok | username is unique")
    void createNewStudent_statusIsOk_usernameIsUnique() throws Exception {
        ResponseDto responseDto = ResponseDto.builder()
                .status(HttpStatus.CREATED.name())
                .message("Student successfully created with id='1'")
                .build();

        when(studentService.createNewStudent(any(StudentRegisterRequest.class)))
                .thenReturn(responseDto);

        mvc.perform(post("/api/students/reg")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(studentRegisterRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.name()))
                .andExpect(jsonPath("$.message").value("Student successfully created with id='1'"));
    }

    @Test
    @WithMockUser
    @DisplayName("create new student | noUniqueObjectException | username is not unique")
    void createNewStudent_statusIsConflict_usernameIsUnique() throws Exception {
        doThrow(NoUniqueObjectException.class)
                .when(studentService)
                .createNewStudent(any(StudentRegisterRequest.class));

        mvc.perform(post("/api/students/reg")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(studentRegisterRequest)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

}
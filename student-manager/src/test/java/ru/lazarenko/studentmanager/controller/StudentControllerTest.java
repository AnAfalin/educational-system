package ru.lazarenko.studentmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.lazarenko.studentmanager.dto.CardDto;
import ru.lazarenko.studentmanager.dto.ResponseDto;
import ru.lazarenko.studentmanager.dto.StudentDto;
import ru.lazarenko.studentmanager.exception.NoFoundElementException;
import ru.lazarenko.studentmanager.service.StudentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
class StudentControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    StudentService studentService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("get all students | empty result list | students do not exist")
    void getAllStudents_emptyResultList_studentsDoNotExist() throws Exception {
        when(studentService.getAllStudents())
                .thenReturn(List.of());

        mvc.perform(get("/api/students")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser
    @DisplayName("get all students | not empty result list | students exist")
    void getAllStudents_notEmptyResultList_studentsExist() throws Exception {
        StudentDto studentDto = StudentDto.builder()
                .id(1)
                .firstname("Mike")
                .build();

        when(studentService.getAllStudents())
                .thenReturn(List.of(studentDto));

        mvc.perform(get("/api/students")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.[0].id").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("get student | status is not found | student do not exist")
    void getStudent_statusIsNotFound_studentDoNotExist() throws Exception {
        doThrow(NoFoundElementException.class)
                .when(studentService)
                .getStudentById(anyInt());

        mvc.perform(get("/api/students/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    @WithMockUser
    @DisplayName("get student | status is Ok | student exist")
    void getStudent_statusOk_studentsExist() throws Exception {
        StudentDto studentDto = StudentDto.builder()
                .id(1)
                .firstname("Mike")
                .build();

        when(studentService.getStudentById(anyInt()))
                .thenReturn(studentDto);

        mvc.perform(get("/api/students/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("add card | status is not found | student do not exist")
    void addCard_statusIsNotFound_studentDoNotExist() throws Exception {
        CardDto cardDto = new CardDto();
        cardDto.setNumber("11111");

        doThrow(NoFoundElementException.class)
                .when(studentService)
                .addCard(anyInt(), any(CardDto.class));

        mvc.perform(post("/api/students/1/add-card")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(cardDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").doesNotExist());
    }

    @Test
    @WithMockUser
    @DisplayName("add card | status is ok | student exist")
    void addCard_statusIsOk_studentExist() throws Exception {
        CardDto cardDto = new CardDto();
        cardDto.setNumber("11111");

        ResponseDto responseDto = ResponseDto.builder()
                .status(HttpStatus.OK.name())
                .message("Card with number='%s' successfully saved for user with id='%s'"
                        .formatted(cardDto.getNumber(), 1))
                .build();

        when(studentService.addCard(anyInt(), any(CardDto.class)))
                .thenReturn(responseDto);

        mvc.perform(post("/api/students/1/add-card")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(cardDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Card with number='11111' successfully saved for user with id='1'"));
    }
}
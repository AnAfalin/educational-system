package ru.lazarenko.paymentservice.controller;

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
import ru.lazarenko.model.dto.ResponseDto;
import ru.lazarenko.paymentservice.dto.SignUpRequest;
import ru.lazarenko.paymentservice.exception.CourseRegistrationException;
import ru.lazarenko.paymentservice.service.CourseRegistrationService;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseRegistrationController.class)
class CourseRegistrationControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    CourseRegistrationService courseRegistrationService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("top up balance | status is ok | request is correct")
    void signUpForCourse_statusIsOk_requestIsCorrect() throws Exception {
        SignUpRequest request = SignUpRequest.builder()
                .courseId(10)
                .studentId(1)
                .build();

        ResponseDto response = ResponseDto.builder()
                .status(HttpStatus.OK.name())
                .message("Student with id='%s' successful sign up for course '%s' with id='%s'"
                        .formatted(request.getStudentId(), "Java Core", request.getCourseId()))
                .build();

        when(courseRegistrationService.signUpNewUser(anyInt(), anyInt()))
                .thenReturn(response);

        mvc.perform(post("/api/course-record/sign-up")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Student with id='1' successful sign up for course 'Java Core' with id='10'"));
    }

    @Test
    @WithMockUser
    @DisplayName("top up balance | status is Forbidden | no enough money om the balance")
    void signUpForCourse_statusIsForbidden_noEnoughMoneyOnBalance() throws Exception {
        SignUpRequest request = SignUpRequest.builder()
                .courseId(10)
                .studentId(1)
                .build();

        doThrow(CourseRegistrationException.class)
                .when(courseRegistrationService)
                .signUpNewUser(anyInt(), anyInt());

        mvc.perform(post("/api/course-record/sign-up")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}
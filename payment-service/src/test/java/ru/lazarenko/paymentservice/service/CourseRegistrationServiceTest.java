package ru.lazarenko.paymentservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.lazarenko.model.dto.account.AccountDto;
import ru.lazarenko.model.dto.course.CategoryDto;
import ru.lazarenko.model.dto.course.CourseDto;
import ru.lazarenko.model.dto.student.StudentDto;
import ru.lazarenko.paymentservice.client.CourseClient;
import ru.lazarenko.paymentservice.client.StudentClient;
import ru.lazarenko.paymentservice.exception.CourseRegistrationException;
import ru.lazarenko.paymentservice.repository.CourseRecordRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
class CourseRegistrationServiceTest {
    @Autowired
    CourseRegistrationService underTest;

    @MockBean
    CourseClient courseClient;

    @MockBean
    StudentClient studentClient;

    @MockBean
    CourseRecordRepository courseRecordRepository;

    CourseDto courseDto;
    StudentDto studentDto;

    @BeforeEach
    void prepare() {
        courseDto = CourseDto.builder()
                .countFreePlace(10)
                .countPlace(10)
                .id(1)
                .name("Java Core")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusMonths(1))
                .category(new CategoryDto(1, "IT"))
                .price(new BigDecimal(100000))
                .build();

        studentDto = StudentDto.builder()
                .id(11)
                .numberCard("0000 4444 8888 7777")
                .firstname("Михаил")
                .lastName("Смирнов")
                .patronymic("Андреевич")
                .account(new AccountDto(1, new BigDecimal(150000)))
                .build();

    }


    @Test
    void signUpNewUser_CourseRegistrationException_noFreePlaces() {
        courseDto.setCountFreePlace(0);

        when(courseClient.getCourseById(anyInt()))
                .thenReturn(courseDto);

        when(studentClient.getStudentById(anyInt()))
                .thenReturn(studentDto);

        assertThrows(CourseRegistrationException.class, () -> underTest.signUpNewUser(courseDto.getId(), studentDto.getId()));
    }

    @Test
    void signUpNewUser_CourseRegistrationException_noEnoughMoney() {
        studentDto.getAccount().setBalance(new BigDecimal(0));

        when(courseClient.getCourseById(anyInt()))
                .thenReturn(courseDto);

        when(studentClient.getStudentById(anyInt()))
                .thenReturn(studentDto);

        assertThrows(CourseRegistrationException.class, () -> underTest.signUpNewUser(courseDto.getId(), studentDto.getId()));
    }

    @Test
    void signUpNewUser_successfullySignUp_allConditionalsAreCorrect() {

        when(courseClient.getCourseById(anyInt()))
                .thenReturn(courseDto);

        when(studentClient.getStudentById(anyInt()))
                .thenReturn(studentDto);


        underTest.signUpNewUser(courseDto.getId(), studentDto.getId());
        verify(studentClient, times(1))
                .decreaseBalanceAccountById(anyInt(), any());

        verify(courseClient, times(1))
                .decreaseFreePlaceCourseById(anyInt());

        verify(courseRecordRepository, times(1))
                .save(any());
    }
}
package ru.lazarenko.studentmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import ru.lazarenko.studentmanager.client.UserClient;
import ru.lazarenko.studentmanager.dto.*;
import ru.lazarenko.studentmanager.entity.Student;
import ru.lazarenko.studentmanager.exception.NoCorrectCardNumber;
import ru.lazarenko.studentmanager.exception.NoFoundElementException;
import ru.lazarenko.studentmanager.exception.NoUniqueObjectException;
import ru.lazarenko.studentmanager.repository.AccountRepository;
import ru.lazarenko.studentmanager.repository.StudentRepository;
import ru.lazarenko.studentmanager.service.mapper.RegisterMapper;
import ru.lazarenko.studentmanager.service.mapper.StudentMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
class StudentServiceTest {
    @Autowired
    StudentService underTest;

    @MockBean
    UserClient userClient;

    @MockBean
    StudentRepository studentRepository;

    @MockBean
    StudentMapper studentMapper;

    @MockBean
    RegisterMapper registerMapper;

    StudentRegisterRequest studentRegisterRequest;
    StudentDto studentDto;
    Student student;
    UserRegisterRequest userRegisterRequest;
    UserRegisterResponse userRegisterResponse;
    CardDto cardDto;


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

        studentDto = StudentDto.builder()
                .id(1)
                .firstname("Михаил")
                .lastName("Смирнов")
                .patronymic("Дмитриевич")
                .build();

        student = Student.builder()
                .id(1)
                .firstname("Михаил")
                .lastName("Смирнов")
                .patronymic("Дмитриевич")
                .build();

        userRegisterRequest = UserRegisterRequest.builder()
                .username("mike")
                .password("password")
                .build();

        userRegisterResponse = UserRegisterResponse.builder()
                .id(1)
                .username("mike")
                .registrationDate(LocalDate.now())
                .roles(List.of(new RoleDto("USER")))
                .build();
    }

    @Test
    @DisplayName("create new student | noUniqueObjectException | username exist")
    void createNewStudent_noUniqueObjectException_usernameExist() {
        when(userClient.checkUniqueUsername(anyString()))
                .thenReturn(true);

        assertThrows(NoUniqueObjectException.class, () -> underTest.createNewStudent(studentRegisterRequest));
        verify(userClient, times(1)).checkUniqueUsername(anyString());
        verify(userClient, times(0)).createNewUser(any());
    }

    @Test
    @DisplayName("create new student | correct response and created student | username doe not exist")
    void createNewStudent_correctResponseAndCreatedStudent_usernameDoeNotExist() {
        when(userClient.checkUniqueUsername(anyString()))
                .thenReturn(false);

        when(registerMapper.toUserRegister(any()))
                .thenReturn(userRegisterRequest);

        when(userClient.createNewUser(any(UserRegisterRequest.class)))
                .thenReturn(userRegisterResponse);

        when(studentMapper.toStudent(any(StudentRegisterRequest.class)))
                .thenReturn(student);

        when(studentRepository.save(any(Student.class)))
                .thenReturn(student);

        underTest.createNewStudent(studentRegisterRequest);
        verify(studentRepository, times(1)).save(any());

    }

    @Test
    @DisplayName("add card | NoFoundElementException | student does not exist")
    void addCard_noFoundElementException_studentDoesNotExist() {
        cardDto = new CardDto();
        cardDto.setNumber("0000 0000 7894 7489");

        when(studentRepository.findStudentByWithAccountById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NoFoundElementException.class, () -> underTest.addCard(100, cardDto));
        verify(studentRepository, times(1)).findStudentByWithAccountById(anyInt());
        verify(studentRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("add card | correct response and add card | student exist")
    void addCard_correctResponseAndAddCard_studentExist() {
        cardDto = new CardDto();
        cardDto.setNumber("0000 0000 7894 7489");

        when(studentRepository.findStudentByWithAccountById(anyInt()))
                .thenReturn(Optional.of(student));

        underTest.addCard(100, cardDto);
        verify(studentRepository, times(1)).findStudentByWithAccountById(anyInt());
        verify(studentRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("add card | NoCorrectCardNumber | username exist and number card is incorrect")
    void addCard_noCorrectCardNumber_usernameExistAndNumberCardIsIncorrect() {
        cardDto = new CardDto();
        cardDto.setNumber("7894 7489");

        when(studentRepository.findStudentByWithAccountById(anyInt()))
                .thenReturn(Optional.of(student));

        assertThrows(NoCorrectCardNumber.class, () -> underTest.addCard(100, cardDto));
        verify(studentRepository, times(1)).findStudentByWithAccountById(anyInt());
        verify(studentRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("get all students | empty result list | students do not exist")
    void getAllStudents_emptyResultList_studentsDoNotExist() {
        when(studentRepository.findAll())
                .thenReturn(List.of(student));

        when(studentMapper.toStudentDtoList(anyList()))
                .thenReturn(List.of(studentDto));

        List<StudentDto> result = underTest.getAllStudents();

        assertFalse(result.isEmpty());
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(1);
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("get all students | not empty result list | students exist")
    void getAllStudents_notEmptyResultList_studentsExist() {
        when(studentRepository.findAll())
                .thenReturn(List.of());

        when(studentMapper.toStudentDtoList(anyList()))
                .thenReturn(List.of());

        List<StudentDto> result = underTest.getAllStudents();

        assertTrue(result.isEmpty());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("get student by id | NoFoundElementException | student does not exist")
    void getStudentById_noFoundElementException_studentDoesNotExist() {
        when(studentRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(NoFoundElementException.class, () -> underTest.getStudentById(100));
        verify(studentRepository, times(1)).findById(anyInt());
    }

    @Test
    @DisplayName("get student by id | returned studentDto | student exist")
    void getStudentById_correctReturnedStudentDto_studentExist() {
        when(studentRepository.findById(anyInt()))
                .thenReturn(Optional.of(student));

        when(studentMapper.toStudentDto(any(Student.class)))
                .thenReturn(studentDto);

        StudentDto result = underTest.getStudentById(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getFirstname()).isEqualTo("Михаил");

        verify(studentRepository, times(1)).findById(anyInt());
    }
}
package ru.lazarenko.studentmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lazarenko.model.dto.ResponseDto;
import ru.lazarenko.model.dto.student.StudentDto;
import ru.lazarenko.model.dto.register.UserRegisterResponse;
import ru.lazarenko.studentmanager.client.UserClient;
import ru.lazarenko.studentmanager.dto.*;
import ru.lazarenko.studentmanager.entity.Account;
import ru.lazarenko.studentmanager.entity.Student;
import ru.lazarenko.studentmanager.exception.NoCorrectCardNumber;
import ru.lazarenko.studentmanager.exception.NoFoundElementException;
import ru.lazarenko.studentmanager.exception.NoUniqueObjectException;
import ru.lazarenko.studentmanager.repository.StudentRepository;
import ru.lazarenko.studentmanager.service.mapper.RegisterMapper;
import ru.lazarenko.studentmanager.service.mapper.StudentMapper;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StudentService {
    private final UserClient userClient;
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final RegisterMapper registerMapper;

    @Transactional
    public ResponseDto createNewStudent(StudentRegisterRequest request) {
        if(userClient.checkUniqueUsername(request.getUsername())) {
            throw new NoUniqueObjectException("Username '%s' already exist".formatted(request.getUsername()));
        }

        UserRegisterResponse userRegisterResponse = userClient.createNewUser(registerMapper.toUserRegister(request));

        Student student = studentMapper.toStudent(request);
        Account account = new Account();

        student.setUserId(userRegisterResponse.getId());
        student.setAccount(account);

        Student savedStudent = studentRepository.save(student);

        return ResponseDto.builder()
                .status(HttpStatus.CREATED.name())
                .message("Student successfully created with id='%s'".formatted(savedStudent.getId()))
                .build();
    }

    @Transactional
    public ResponseDto addCard(Integer studentId, CardDto cardDto) {
        Student student = studentRepository.findStudentByWithAccountById(studentId)
                .orElseThrow(() -> new NoFoundElementException("Student with id='%s' not found"));

        String numberCard = cardDto.getNumber().replace(" ", "");
        validateCardNumber(numberCard);

        student.setNumberCard(numberCard);

        studentRepository.save(student);

        return ResponseDto.builder()
                .status(HttpStatus.OK.name())
                .message("Card with number='%s' successfully saved for user with id='%s'"
                        .formatted(numberCard, student.getId()))
                .build();
    }

    @Transactional(readOnly = true)
    public List<StudentDto> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return studentMapper.toStudentDtoList(students);
    }

    @Transactional(readOnly = true)
    public StudentDto getStudentById(Integer id) {
        Student foundStudent = studentRepository.findById(id)
                .orElseThrow(() -> new NoFoundElementException("Student wit id='%s' not found".formatted(id)));

        return studentMapper.toStudentDto(foundStudent);
    }

    private void validateCardNumber(String cardNumber) {
        if(!cardNumber.matches("[0-9]*")) {
            throw new NoCorrectCardNumber("Number card contains only numbers");
        }

        int lengthNumberCard = cardNumber.length();
        int[] numberLengths = new int[]{13, 15, 16, 19};

        boolean numberIsCorrect = false;
        for (int numberLength : numberLengths) {
            if(lengthNumberCard == numberLength) {
                numberIsCorrect = true;
                break;
            }
        }
        if(!numberIsCorrect) {
            throw new NoCorrectCardNumber("Count of number card is not correct");
        }
    }
}
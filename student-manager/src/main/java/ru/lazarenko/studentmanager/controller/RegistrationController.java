package ru.lazarenko.studentmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.lazarenko.studentmanager.dto.ResponseDto;
import ru.lazarenko.studentmanager.dto.StudentRegisterRequest;
import ru.lazarenko.studentmanager.service.StudentService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class RegistrationController {
    private final StudentService studentService;

    @PostMapping("/api/students/reg")
    public ResponseDto createNewStudent(@Valid @RequestBody StudentRegisterRequest request) {
        return studentService.createNewStudent(request);
    }
}

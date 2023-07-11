package ru.lazarenko.studentmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.lazarenko.model.dto.ResponseDto;
import ru.lazarenko.model.dto.student.StudentDto;
import ru.lazarenko.studentmanager.dto.CardDto;
import ru.lazarenko.studentmanager.service.StudentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService studentService;

    @PostMapping("/{id}/add-card")
    public ResponseDto addCard(@PathVariable Integer id, @Valid CardDto cardDto) {
        return studentService.addCard(id, cardDto);
    }

    @GetMapping
    public List<StudentDto> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public StudentDto getStudent(@PathVariable Integer id) {
        return studentService.getStudentById(id);
    }
}

package ru.lazarenko.paymentservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.lazarenko.model.dto.ResponseDto;
import ru.lazarenko.paymentservice.dto.SignUpRequest;
import ru.lazarenko.paymentservice.service.CourseRegistrationService;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/course-record")
public class CourseRegistrationController {
    private final CourseRegistrationService courseRegistrationService;

    @PostMapping("/sign-up")
    public ResponseDto signUpForCourse(@Valid @RequestBody SignUpRequest request) {
        return courseRegistrationService.signUpNewUser(request.getCourseId(), request.getStudentId());
    }
}

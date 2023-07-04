package ru.lazarenko.coursemanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.lazarenko.coursemanager.dto.CourseDto;
import ru.lazarenko.coursemanager.dto.ResponseDto;
import ru.lazarenko.coursemanager.service.CourseService;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/courses")
@RestController
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public List<CourseDto> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
    public CourseDto getCourse(@PathVariable(name = "id") Integer id) {
        return courseService.getCourseById(id);
    }

    @PostMapping
    public ResponseDto saveCourse(@RequestBody @Valid CourseDto courseDto) {
        return courseService.saveNewCourse(courseDto);
    }

    @PostMapping("/{id}/decrease-free-place")
    public ResponseDto decreaseFreePlace(@PathVariable(name = "id") Integer id) {
        return courseService.decreaseFreePlace(id);
    }
}

package ru.lazarenko.coursemanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.lazarenko.coursemanager.service.CourseService;
import ru.lazarenko.model.dto.ResponseDto;
import ru.lazarenko.model.dto.course.CourseDto;

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
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto saveCourse(@RequestBody @Valid CourseDto courseDto) {
        return courseService.saveNewCourse(courseDto);
    }

    @PutMapping("/{id}/decrease-free-place")
    public ResponseDto decreaseFreePlace(@PathVariable(name = "id") Integer id) {
        return courseService.decreaseFreePlace(id);
    }
}

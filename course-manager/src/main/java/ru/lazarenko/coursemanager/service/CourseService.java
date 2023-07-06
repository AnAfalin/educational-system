package ru.lazarenko.coursemanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lazarenko.coursemanager.dto.ResponseDto;
import ru.lazarenko.coursemanager.dto.CourseDto;
import ru.lazarenko.coursemanager.entity.Course;
import ru.lazarenko.coursemanager.exception.NoFoundElementException;
import ru.lazarenko.coursemanager.exception.NoUniqueObjectException;
import ru.lazarenko.coursemanager.repository.CourseRepository;
import ru.lazarenko.coursemanager.service.mapper.CourseMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Transactional(readOnly = true)
    public List<CourseDto> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courseMapper.toCourseDtoList(courses);
    }

    @Transactional(readOnly = true)
    public CourseDto getCourseById(Integer id) {
        Course foundCourse = courseRepository.findById(id)
                .orElseThrow(() -> new NoFoundElementException("Course wit id='%s' not found".formatted(id)));

        return courseMapper.toCourseDto(foundCourse);
    }

    @Transactional
    public ResponseDto saveNewCourse(CourseDto courseDto) {
        checkExistCategoryName(courseDto.getName());

        Course course = courseMapper.toCourse(courseDto);

        Course savedCourse = courseRepository.save(course);
        return ResponseDto.builder()
                .status(HttpStatus.CREATED.name())
                .message("Course successful was create with id='%s'".formatted(savedCourse.getId()))
                .build();
    }

    @Transactional
    public ResponseDto decreaseFreePlace(Integer id) {
        Course foundCourse = courseRepository.findById(id)
                .orElseThrow(() -> new NoFoundElementException("Course wit id='%s' not found".formatted(id)));

        int newCountFreePlace = foundCourse.getCountFreePlace() - 1;

        foundCourse.setCountFreePlace(newCountFreePlace);
        Course savedCourse = courseRepository.save(foundCourse);

        return ResponseDto.builder()
                .status(HttpStatus.OK.name())
                .message("Free place on course with id='%s' successfully decrease. Actual free places now is=%s"
                        .formatted(savedCourse.getId(), savedCourse.getCountFreePlace()))
                .build();
    }

    private void checkExistCategoryName(String name) {
        if (courseRepository.findByName(name).isPresent()) {
            throw new NoUniqueObjectException("Course with name='%s' already exist".formatted(name));
        }
    }

}

package ru.lazarenko.coursemanager.service.mapper;

import org.mapstruct.Mapper;
import ru.lazarenko.coursemanager.entity.Course;
import ru.lazarenko.model.dto.course.CourseDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    Course toCourse(CourseDto courseDto);

    CourseDto toCourseDto(Course course);

    List<CourseDto> toCourseDtoList(List<Course> courses);
}

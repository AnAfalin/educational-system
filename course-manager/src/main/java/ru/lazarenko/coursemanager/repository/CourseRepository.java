package ru.lazarenko.coursemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lazarenko.coursemanager.entity.Course;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    Optional<Course> findByName(String name);
}

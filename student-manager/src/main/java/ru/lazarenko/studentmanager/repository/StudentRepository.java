package ru.lazarenko.studentmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.lazarenko.studentmanager.entity.Student;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    @Query(value = "select s from Student s left join fetch s.account where s.id=:studentId")
    Optional<Student> findStudentByWithAccountById(Integer studentId);
}
package ru.lazarenko.studentmanager.service.mapper;

import org.mapstruct.Mapper;
import ru.lazarenko.model.dto.student.StudentDto;
import ru.lazarenko.studentmanager.dto.StudentRegisterRequest;
import ru.lazarenko.studentmanager.entity.Student;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    Student toStudent(StudentDto categoryDto);

    StudentDto toStudentDto(Student student);

    List<StudentDto> toStudentDtoList(List<Student> students);

    Student toStudent(StudentRegisterRequest studentRegisterRequest);
}

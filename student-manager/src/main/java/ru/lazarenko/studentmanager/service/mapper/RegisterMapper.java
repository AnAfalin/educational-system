package ru.lazarenko.studentmanager.service.mapper;

import org.mapstruct.Mapper;
import ru.lazarenko.studentmanager.dto.StudentRegisterRequest;
import ru.lazarenko.studentmanager.dto.UserRegisterRequest;

@Mapper(componentModel = "spring")
public interface RegisterMapper {
    UserRegisterRequest toUserRegister(StudentRegisterRequest studentRegisterRequest);
}
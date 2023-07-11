package ru.lazarenko.studentmanager.service.mapper;

import org.mapstruct.Mapper;
import ru.lazarenko.model.dto.register.UserRegisterRequest;
import ru.lazarenko.studentmanager.dto.StudentRegisterRequest;

@Mapper(componentModel = "spring")
public interface RegisterMapper {
    UserRegisterRequest toUserRegister(StudentRegisterRequest studentRegisterRequest);
}
package ru.lazarenko.securitymanager.service.mapper;

import org.mapstruct.Mapper;
import ru.lazarenko.model.dto.register.UserRegisterResponse;
import ru.lazarenko.securitymanager.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserRegisterResponse toUserDto(User user);

    List<UserRegisterResponse> toListUserDto(List<User> users);
}

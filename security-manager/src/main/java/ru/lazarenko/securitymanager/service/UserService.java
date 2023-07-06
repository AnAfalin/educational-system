package ru.lazarenko.securitymanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lazarenko.securitymanager.dto.UserRegisterResponse;
import ru.lazarenko.securitymanager.dto.UserRegisterRequest;
import ru.lazarenko.securitymanager.entity.Role;
import ru.lazarenko.securitymanager.entity.User;
import ru.lazarenko.securitymanager.model.UserRole;
import ru.lazarenko.securitymanager.repository.UserRepository;
import ru.lazarenko.securitymanager.service.mapper.UserMapper;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserRegisterResponse createUser(UserRegisterRequest request) {
        Role role = new Role();
        role.setName(UserRole.USER);

        User user = User.builder()
                .username(request.getUsername())
                .registrationDate(LocalDate.now())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of(role))
                .build();

        User savedUser = userRepository.save(user);

        return userMapper.toUserDto(savedUser);
    }

    @Transactional
    public List<UserRegisterResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toListUserDto(users);
    }

    @Transactional(readOnly = true)
    public Boolean checkExistUsername(String username) {
        return userRepository.checkExistUsername(username) > 0;
    }
}
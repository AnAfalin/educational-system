package ru.lazarenko.studentmanager.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRegisterRequest {

    @NotBlank(message = "Username cannot be empty or null")
    @Pattern(regexp = "[A-Za-z0-9]{3,15}",
            message = "Username must contains 3-15 characters (uppercase letters, lowercase letters or numbers )")
    private String username;

    @NotBlank(message = "Email cannot be empty or null")
    @Email(regexp = "[\\w._]{1,10}@[\\w]{2,}.[\\w]{2,}", message = "Email must be in format as email (email@email.com)")
    private String email;

    @NotBlank(message = "Password cannot be empty or null")
    @Pattern(regexp = "[A-Za-z0-9._]{5,15}",
            message = "Password must contains 5-15 characters (uppercase letters, lowercase letters or numbers)")
    private String password;

    @NotEmpty(message = "Firstname cannot be null or empty")
    private String firstname;

    @NotEmpty(message = "Firstname cannot be null or empty")
    private String lastName;

    @NotEmpty(message = "Patronymic cannot be null or empty")
    private String patronymic;
}
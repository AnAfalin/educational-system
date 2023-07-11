package ru.lazarenko.model.dto.register;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {
    @NotBlank(message = "Username cannot be empty or null")
    @Pattern(regexp = "[A-Za-z0-9]{3,15}",
            message = "Username must contains 3-15 characters (uppercase letters, lowercase letters or numbers )")
    private String username;

    @NotBlank(message = "Password cannot be empty or null")
    @Pattern(regexp = "[A-Za-z0-9._!#$%&]{5,25}",
            message = "Password must contains 5-15 characters (uppercase letters, lowercase letters or numbers)")
    private String password;

}
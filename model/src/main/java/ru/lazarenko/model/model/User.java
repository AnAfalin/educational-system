package ru.lazarenko.model.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String username;

    private String password;

    private List<Role> roles;

}

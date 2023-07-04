package ru.lazarenko.studentmanager.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentDto {
    private Integer id;

    private String firstname;

    private String lastName;

    private String patronymic;

    private String numberCard;

    private AccountDto account;

}

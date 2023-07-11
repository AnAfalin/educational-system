package ru.lazarenko.model.dto.student;

import lombok.*;
import ru.lazarenko.model.dto.account.AccountDto;

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

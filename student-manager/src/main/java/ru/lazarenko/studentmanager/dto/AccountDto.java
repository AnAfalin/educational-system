package ru.lazarenko.studentmanager.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    private Integer id;

    private BigDecimal balance;
}

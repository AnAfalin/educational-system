package ru.lazarenko.model.dto.account;

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

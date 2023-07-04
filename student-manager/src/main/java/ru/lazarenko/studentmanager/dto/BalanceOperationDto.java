package ru.lazarenko.studentmanager.dto;

import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
public class BalanceOperationDto {
    @NotNull(message = "Sum cannot be null")
    @Min(value = 0)
    private BigDecimal sum;
}

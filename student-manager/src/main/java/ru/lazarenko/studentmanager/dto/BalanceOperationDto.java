package ru.lazarenko.studentmanager.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class BalanceOperationDto {
    @NotNull(message = "Sum cannot be null")
    @Min(value = 0)
    private BigDecimal sum;
}

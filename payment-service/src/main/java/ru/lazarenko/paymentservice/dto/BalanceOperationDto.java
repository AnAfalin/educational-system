package ru.lazarenko.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BalanceOperationDto {
    @NotNull(message = "Sum cannot be null")
    @Min(value = 0)
    private BigDecimal sum;
}

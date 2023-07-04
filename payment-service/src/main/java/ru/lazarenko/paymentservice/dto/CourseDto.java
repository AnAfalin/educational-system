package ru.lazarenko.paymentservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {
    private Integer id;

    private String name;

    private CategoryDto category;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal price;

    private Integer countPlace;

    private Integer countFreePlace;

}

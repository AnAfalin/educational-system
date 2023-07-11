package ru.lazarenko.model.dto.course;

import lombok.*;
import ru.lazarenko.model.constraint.EndDateIsAfterStartDate;

import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EndDateIsAfterStartDate
public class CourseDto {
    private Integer id;

    @NotEmpty(message = "Course name cannot be null or empty")
    private String name;

    @Valid
    @NotNull(message = "Category cannot be null")
    private CategoryDto category;

    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent(message = "Start date must be in the future or in the present")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    private LocalDate endDate;

    @NotNull(message = "Price cannot be null")
    private BigDecimal price;

    @NotNull(message = "Count place of course cannot be null")
    private Integer countPlace;

    private Integer countFreePlace;

}

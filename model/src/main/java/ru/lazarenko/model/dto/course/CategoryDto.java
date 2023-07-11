package ru.lazarenko.model.dto.course;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private Integer id;

    @NotEmpty(message = "Category name cannot be null or empty")
    private String name;
}
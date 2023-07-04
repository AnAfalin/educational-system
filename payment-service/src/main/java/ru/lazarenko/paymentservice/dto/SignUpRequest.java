package ru.lazarenko.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class SignUpRequest {
    @NotNull(message = "Course id cannot be null")
    private Integer courseId;

    @NotNull(message = "Student id cannot be null")
    private Integer studentId;
}

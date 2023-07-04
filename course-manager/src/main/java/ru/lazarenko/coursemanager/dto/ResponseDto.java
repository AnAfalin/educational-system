package ru.lazarenko.coursemanager.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@Getter
public class ResponseDto {
    private String status;
    private String message;
}

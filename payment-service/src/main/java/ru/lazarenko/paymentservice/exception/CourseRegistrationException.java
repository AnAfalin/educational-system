package ru.lazarenko.paymentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class CourseRegistrationException extends RuntimeException {
    public CourseRegistrationException(String message) {
        super(message);
    }
}

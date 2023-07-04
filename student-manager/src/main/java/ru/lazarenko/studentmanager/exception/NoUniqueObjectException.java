package ru.lazarenko.studentmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class NoUniqueObjectException extends RuntimeException {

    public NoUniqueObjectException(String message) {
        super(message);
    }
}

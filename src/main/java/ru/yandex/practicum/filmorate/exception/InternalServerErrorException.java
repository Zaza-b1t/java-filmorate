package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends AbstractDtoException {
    public InternalServerErrorException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

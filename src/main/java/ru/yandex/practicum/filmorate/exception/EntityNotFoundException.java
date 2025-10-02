package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends AbstractDtoException {
    public EntityNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}

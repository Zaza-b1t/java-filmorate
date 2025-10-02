package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatusCode;
import ru.yandex.practicum.filmorate.dto.ErrorResponse;

public abstract class AbstractDtoException extends RuntimeException {
    private final HttpStatusCode statusCode;


    public AbstractDtoException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ErrorResponse toResponse() {
        return new ErrorResponse(getMessage(), statusCode);

    }
}

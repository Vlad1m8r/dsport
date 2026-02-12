package ru.weu.dsport.exception;

import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {

    private final String code;
    private final Object data;

    public ConflictException(String message, String code, Object data) {
        super(message);
        this.code = code;
        this.data = data;
    }
}

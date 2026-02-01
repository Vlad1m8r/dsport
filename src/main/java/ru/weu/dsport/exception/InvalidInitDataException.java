package ru.weu.dsport.exception;

public class InvalidInitDataException extends RuntimeException {

    public InvalidInitDataException(String message) {
        super(message);
    }

    public InvalidInitDataException(String message, Throwable cause) {
        super(message, cause);
    }
}

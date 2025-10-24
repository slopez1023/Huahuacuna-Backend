package com.huahuacuna.exception;

/**
 * Excepción lanzada cuando se intenta registrar un usuario con un email ya existente
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

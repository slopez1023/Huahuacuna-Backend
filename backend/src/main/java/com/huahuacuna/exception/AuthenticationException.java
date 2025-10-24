package com.huahuacuna.exception;

/**
 * Excepción personalizada para errores de autenticación.
 * <p>
 * Esta clase permite manejar de forma centralizada los errores relacionados
 * con el proceso de autenticación, como credenciales inválidas o usuario no encontrado.
 * </p>
 */
public class AuthenticationException extends RuntimeException {

    /**
     * Constructor que crea una nueva excepción con un mensaje descriptivo.
     *
     * @param message el mensaje que describe el error de autenticación.
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * Constructor que crea una nueva excepción con un mensaje descriptivo
     * y una causa original.
     *
     * @param message el mensaje que describe el error de autenticación.
     * @param cause   la excepción que causó este error.
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}

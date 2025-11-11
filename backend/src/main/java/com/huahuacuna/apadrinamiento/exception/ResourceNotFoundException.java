

package com.huahuacuna.apadrinamiento.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada que se lanza cuando no se encuentra un recurso.
 * Al anotarla con @ResponseStatus(HttpStatus.NOT_FOUND), Spring
 * automáticamente devolverá un error 404 Not Found al cliente.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

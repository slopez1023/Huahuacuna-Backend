package com.huahuacuna.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para la aplicación.
 * <p>
 * Esta clase centraliza la captura y transformación de las excepciones
 * lanzadas por los controladores en respuestas HTTP estructuradas y coherentes.
 * </p>
 *
 * <p>Proporciona un formato uniforme de salida usando {@link ErrorResponse},
 * facilitando el mantenimiento, la depuración y una mejor experiencia de usuario
 * en el cliente.</p>
 *
 * <p>Las excepciones manejadas incluyen:</p>
 * <ul>
 *   <li>{@link AuthenticationException}: errores de autenticación (401)</li>
 *   <li>{@link MethodArgumentNotValidException}: errores de validación (400)</li>
 *   <li>{@link Exception}: errores genéricos no controlados (500)</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja las excepciones personalizadas relacionadas con la autenticación.
     * <p>
     * Cuando ocurre un error de autenticación (por ejemplo, credenciales inválidas),
     * se devuelve una respuesta HTTP con el código 401 (Unauthorized) y un cuerpo
     * que contiene información sobre el error.
     * </p>
     *
     * @param ex      la excepción {@link AuthenticationException} capturada.
     * @param request el objeto {@link WebRequest} que contiene información sobre la solicitud.
     * @return una respuesta {@link ResponseEntity} con el cuerpo {@link ErrorResponse}.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Authentication Failed")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }

    /**
     * Maneja las excepciones generadas por errores de validación de datos de entrada.
     * <p>
     * Cuando una solicitud contiene datos inválidos (por ejemplo, campos vacíos o con formato incorrecto),
     * se genera una respuesta con código 400 (Bad Request) y un listado de los mensajes de error.
     * </p>
     *
     * @param ex      la excepción {@link MethodArgumentNotValidException} lanzada por Spring.
     * @param request el objeto {@link WebRequest} que representa la solicitud.
     * @return una respuesta {@link ResponseEntity} con detalles del error de validación.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        String messages = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message(messages)
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * Maneja cualquier otra excepción no controlada de manera específica.
     * <p>
     * Este método actúa como una red de seguridad para capturar errores inesperados,
     * devolviendo una respuesta con el estado 500 (Internal Server Error) y un mensaje genérico.
     * </p>
     *
     * @param ex      la excepción genérica capturada.
     * @param request el objeto {@link WebRequest} que contiene la información de la solicitud.
     * @return una respuesta {@link ResponseEntity} con un mensaje genérico de error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Ocurrió un error inesperado. Por favor, intenta más tarde.")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}

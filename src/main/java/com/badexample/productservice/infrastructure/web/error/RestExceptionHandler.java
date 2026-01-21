package com.badexample.productservice.infrastructure.web.error;

import com.badexample.productservice.application.exception.ProductNotFoundException;
import com.badexample.productservice.infrastructure.web.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    private ApiErrorResponse buildError(int status, String message, String path, String error, Object details){

        ApiErrorResponse errorResponse = new ApiErrorResponse();
        errorResponse.setStatus(status);
        errorResponse.setMessage(message);
        errorResponse.setPath(path);
        errorResponse.setDetails(details);
        errorResponse.setError(error);
        errorResponse.setTimestamp(Instant.now());

        return errorResponse;
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProductNotFound(
            ProductNotFoundException ex,
            HttpServletRequest request){

        ApiErrorResponse errorResponse = buildError(HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getRequestURI(),
                "No encontrado",
                Map.of("productId", ex.getProductId()));


        return ResponseEntity
                .status(errorResponse.getStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request){

        ApiErrorResponse errorResponse = buildError(HttpStatus.BAD_REQUEST.value(),
                "Validacion Fallida",
                request.getRequestURI(),
                "Solicitud invalida",
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(err -> err.getField() + ": " + err.getDefaultMessage()).toList());

        return ResponseEntity
                .status(errorResponse.getStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request){

        ApiErrorResponse errorResponse = buildError(HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI(),
                "Solicitud invalida",
                null);

        return ResponseEntity
                .status(errorResponse.getStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request){

        ApiErrorResponse errorResponse = buildError(HttpStatus.BAD_REQUEST.value(),
                "Falta parametro requerido",
                request.getRequestURI(),
                "Solicitud invalida",
                Map.of("parameter", ex.getParameterName()));

        return ResponseEntity
                .status(errorResponse.getStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(
            Exception ex,
            HttpServletRequest request){

        ApiErrorResponse errorResponse = buildError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocurrio un error inesperado",
                request.getRequestURI(),
                "Error interno del servidor",
                null);

        return ResponseEntity
                .status(errorResponse.getStatus())
                .body(errorResponse);
    }

}

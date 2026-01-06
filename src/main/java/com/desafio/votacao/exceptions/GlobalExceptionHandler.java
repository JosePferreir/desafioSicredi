package com.desafio.votacao.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        log.warn("Erro de validação: {}", errors);

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação nos campos",
                LocalDateTime.now(),
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleBusinessException(IllegalStateException ex) {
        log.warn("Violação de regra de negócio: {}", ex.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("Recurso não encontrado: {}", ex.getMessage());

        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleJsonErrors(HttpMessageNotReadableException ex) {
        Map<String, String> errors = null;

        if (ex.getCause() instanceof InvalidFormatException ifx && ifx.getTargetType().isEnum()) {
            String campo = ifx.getPath().get(0).getFieldName();
            String opcoes = Arrays.toString(ifx.getTargetType().getEnumConstants());
            errors = Map.of(campo, "Valor inválido. Opções aceitas: " + opcoes);
            log.warn("Erro de enumeração no JSON: campo '{}' com valor inválido.", campo);
        } else {
            log.error("Erro na leitura do corpo da requisição (JSON): ", ex);
        }

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Erro na leitura do JSON",
                LocalDateTime.now(),
                errors
        );

        return ResponseEntity.status(400).body(apiError);
    }
}

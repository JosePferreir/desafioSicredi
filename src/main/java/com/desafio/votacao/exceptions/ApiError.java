package com.desafio.votacao.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        int status,
        String message,
        LocalDateTime timestamp,
        Map<String, String> validationErrors
) {
}

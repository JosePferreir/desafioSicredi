package com.desafio.votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AssociadoRequest(
        @Schema(description = "Nome completo do associado", example = "João da Silva")
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @Schema(description = "CPF (apenas números)", example = "12345678900")
        @NotBlank(message = "O CPF é obrigatório")
        @Size(min = 11, max = 11, message = "O CPF deve conter exatamente 11 dígitos")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter apenas números")
        String cpf
) {
}

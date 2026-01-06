package com.desafio.votacao.dto;

import com.desafio.votacao.enums.VotoValor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record VotoRequest(
        @Schema(description = "ID da sessão de votação", example = "1")
        @NotNull(message = "votacaoId não pode ser nulo")
        Long votacaoId,

        @Schema(description = "ID do associado que está votando", example = "1")
        @NotNull(message = "associadoId não pode ser nulo")
        Long associadoId,

        @Schema(description = "Voto do associado (SIM ou NAO)", example = "SIM", implementation = VotoValor.class)
        @NotNull(message = "voto não pode ser nulo")
        VotoValor valor
) {
}

package com.desafio.votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record VotacaoRequest(
        @Schema(description = "ID da pauta a ser votada", example = "1")
        @NotNull(message = "O Id da pauta é obrigatório")
        Long pautaId,

        @Schema(description = "Data de início (opcional, default: agora)", example = "2026-01-01T10:00:00")
        @FutureOrPresent(message = "A data de início deve ser atual ou futura")
        LocalDateTime dataInicio,

        @Schema(description = "Tempo de duração em minutos", example = "60", defaultValue = "1")
        Long tempoEmMinutos
) {
}

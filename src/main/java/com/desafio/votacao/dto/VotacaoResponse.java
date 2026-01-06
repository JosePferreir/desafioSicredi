package com.desafio.votacao.dto;

import java.time.LocalDateTime;

public record VotacaoResponse(
        Long id,
        Long pautaId,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        boolean isAberta
) {
}

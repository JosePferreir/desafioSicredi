package com.desafio.votacao.dto;

import com.desafio.votacao.enums.VotoValor;

public record VotoResponse(
        Long id,
        Long votacaoId,
        Long associadoId,
        VotoValor valor
) {
}

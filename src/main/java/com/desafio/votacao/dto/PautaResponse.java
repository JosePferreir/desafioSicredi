package com.desafio.votacao.dto;

import com.desafio.votacao.enums.ResultadoPauta;

public record PautaResponse(
        Long id,
        String titulo,
        String descricao,
        ResultadoPauta resultado
) {
}

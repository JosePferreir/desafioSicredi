package com.desafio.votacao.mapper;

import com.desafio.votacao.dto.VotacaoResponse;
import com.desafio.votacao.model.Pauta;
import com.desafio.votacao.model.Votacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VotacaoMapperTest {

    private final VotacaoMapper mapper = new VotacaoMapper();

    @Test
    @DisplayName("Deve converter Entidade Votacao para VotacaoResponse corretamente")
    void toResponse() {
        Long pautaId = 10L;
        Long votacaoId = 50L;
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fim = inicio.plusMinutes(20);

        Pauta pauta = new Pauta();
        pauta.setId(pautaId);

        Votacao votacao = new Votacao();
        votacao.setId(votacaoId);
        votacao.setPauta(pauta);
        votacao.setDataInicio(inicio);

        votacao.setDataFim(fim);

        VotacaoResponse response = mapper.toResponse(votacao);

        assertNotNull(response);
        assertEquals(votacaoId, response.id());
        assertEquals(pautaId, response.pautaId());
        assertEquals(inicio, response.dataInicio());
        assertEquals(fim, response.dataFim());

        assertEquals(votacao.isAberta(), response.isAberta());
    }
}

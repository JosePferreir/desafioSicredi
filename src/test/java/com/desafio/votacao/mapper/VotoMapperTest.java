package com.desafio.votacao.mapper;

import com.desafio.votacao.dto.VotoRequest;
import com.desafio.votacao.dto.VotoResponse;
import com.desafio.votacao.enums.VotoValor;
import com.desafio.votacao.model.Associado;
import com.desafio.votacao.model.Votacao;
import com.desafio.votacao.model.Voto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VotoMapperTest {

    private final VotoMapper mapper = new VotoMapper();

    @Test
    @DisplayName("Deve converter request para entidade corretamente")
    void toEntity() {
        VotoRequest request = new VotoRequest(1L,2L, VotoValor.SIM);
        Votacao votacao = new Votacao();
        votacao.setId(1L);
        Associado associado = new Associado();
        associado.setId(2L);

        Voto entity = mapper.toEntity(request, votacao, associado);

        assertNotNull(entity);
        assertEquals(request.valor(), entity.getValor());
        assertEquals(votacao, entity.getVotacao());
        assertEquals(associado, entity.getAssociado());
        assertNull(entity.getId());
    }

    @Test
    @DisplayName("Deve converter entidade para response corretamente")
    void toResponse() {
        Votacao votacao = new Votacao();
        votacao.setId(5L);

        Associado associado = new Associado();
        associado.setId(10L);

        Voto entity = new Voto();
        entity.setId(100L);
        entity.setValor(VotoValor.NÃO);
        entity.setVotacao(votacao);
        entity.setAssociado(associado);

        VotoResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(entity.getId(), response.id());
        assertEquals(votacao.getId(), response.votacaoId());
        assertEquals(associado.getId(), response.associadoId());
        assertEquals(entity.getValor(), response.valor());
    }
}

package com.desafio.votacao.mapper;

import com.desafio.votacao.dto.PautaRequest;
import com.desafio.votacao.dto.PautaResponse;
import com.desafio.votacao.enums.ResultadoPauta;
import com.desafio.votacao.model.Pauta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PautaMapperTest {
    private final PautaMapper mapper = new PautaMapper();

    @Test
    @DisplayName("Deve converter Request para Entity corretamente")
    void toEntity() {
        PautaRequest request = new PautaRequest("Aumento", "Detalhes");

        Pauta entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertEquals(request.titulo(), entity.getTitulo());
        assertEquals(request.descricao(), entity.getDescricao());
        assertNull(entity.getId());
    }

    @Test
    @DisplayName("Deve converter Entity para Response corretamente")
    void toResponse() {
        Pauta pauta = new Pauta();
        pauta.setId(1L);
        pauta.setTitulo("Aumento");
        pauta.setDescricao("Detalhes");
        pauta.setResultado(ResultadoPauta.APROVADO);

        PautaResponse response = mapper.toResponse(pauta);

        assertEquals(1L, response.id());
        assertEquals("Aumento", response.titulo());
        assertEquals(ResultadoPauta.APROVADO, response.resultado());
    }
}

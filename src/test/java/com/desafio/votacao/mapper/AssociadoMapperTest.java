package com.desafio.votacao.mapper;

import com.desafio.votacao.dto.AssociadoRequest;
import com.desafio.votacao.dto.AssociadoResponse;
import com.desafio.votacao.model.Associado;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssociadoMapperTest {

    private final AssociadoMapper mapper = new AssociadoMapper();

    @Test
    @DisplayName("Deve converter AssociadoRequest para Entidade Associado corretamente")
    void toEntity() {
        AssociadoRequest request = new AssociadoRequest("João Silva", "12345678900");

        Associado entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertEquals(request.nome(), entity.getNome());
        assertEquals(request.cpf(), entity.getCpf());
        assertNull(entity.getId());
    }

    @Test
    @DisplayName("Deve converter Entidade Associado para AssociadoResponse corretamente")
    void toResponse() {
        Associado entity = new Associado();
        entity.setId(10L);
        entity.setNome("Maria Oliveira");
        entity.setCpf("98765432100");

        AssociadoResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(entity.getId(), response.id());
        assertEquals(entity.getNome(), response.nome());
        assertEquals(entity.getCpf(), response.cpf());
    }
}

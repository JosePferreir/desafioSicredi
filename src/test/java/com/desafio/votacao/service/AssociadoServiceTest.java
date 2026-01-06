package com.desafio.votacao.service;

import com.desafio.votacao.dto.AssociadoRequest;
import com.desafio.votacao.dto.AssociadoResponse;
import com.desafio.votacao.mapper.AssociadoMapper;
import com.desafio.votacao.model.Associado;
import com.desafio.votacao.repository.AssociadoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssociadoServiceTest {
    @InjectMocks
    private AssociadoService associadoService;

    @Mock
    private AssociadoRepository associadoRepository;

    @Mock
    private AssociadoMapper associadoMapper;

    @Test
    @DisplayName("Deve criar um associado com sucesso")
    void criarAssociadoSucesso() {
        AssociadoRequest request = new AssociadoRequest("João Silva", "12345678901");
        Associado associado = new Associado();
        Associado associadoSalvo = new Associado();
        AssociadoResponse response = new AssociadoResponse(1L, "João Silva", "12345678901");

        when(associadoRepository.existsByCpf(request.cpf())).thenReturn(false);
        when(associadoMapper.toEntity(request)).thenReturn(associado);
        when(associadoRepository.save(associado)).thenReturn(associadoSalvo);
        when(associadoMapper.toResponse(associadoSalvo)).thenReturn(response);

        AssociadoResponse resultado = associadoService.criarAssociado(request);

        assertNotNull(resultado);
        assertEquals(request.cpf(), resultado.cpf());
        verify(associadoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF já existe")
    void criarAssociadoErroCpfDuplicado() {
        AssociadoRequest request = new AssociadoRequest("Marcos", "12345678901");
        when(associadoRepository.existsByCpf(request.cpf())).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            associadoService.criarAssociado(request);
        });

        assertEquals("Já existe um associado cadastrado com este CPF.", exception.getMessage());
        verify(associadoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar lista de associados (mesmo que vazia)")
    void buscarAssociadosSucesso() {
        Associado associado = new Associado();
        when(associadoRepository.findAll()).thenReturn(List.of(associado));
        when(associadoMapper.toResponse(any())).thenReturn(new AssociadoResponse(1L, "Nome", "CPF"));

        List<AssociadoResponse> resultado = associadoService.buscarAssociados();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }
}

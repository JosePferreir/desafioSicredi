package com.desafio.votacao.service;

import com.desafio.votacao.dto.PautaRequest;
import com.desafio.votacao.dto.PautaResponse;
import com.desafio.votacao.enums.ResultadoPauta;
import com.desafio.votacao.mapper.PautaMapper;
import com.desafio.votacao.model.Pauta;
import com.desafio.votacao.repository.PautaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PautaServiceTest {
    @InjectMocks
    private PautaService pautaService;

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private PautaMapper pautaMapper;

    @Test
    @DisplayName("Deve criar uma pauta com sucesso")
    void criarPautaSucesso() {
        PautaRequest request = new PautaRequest("Aumento Salarial", "Pauta sobre reajuste");
        Pauta pauta = new Pauta();
        Pauta pautaSalva = new Pauta();
        PautaResponse response = new PautaResponse(1L, "Aumento Salarial", "Pauta sobre reajuste", null);

        when(pautaMapper.toEntity(request)).thenReturn(pauta);
        when(pautaRepository.save(pauta)).thenReturn(pautaSalva);
        when(pautaMapper.toResponse(pautaSalva)).thenReturn(response);

        PautaResponse resultado = pautaService.criarPauta(request);

        assertNotNull(resultado);
        assertEquals("Aumento Salarial", resultado.titulo());
        verify(pautaRepository).save(any(Pauta.class));
    }

    @Test
    @DisplayName("Deve buscar todas as pautas com sucesso")
    void buscarPautasSucesso() {
        Pauta pauta = new Pauta();
        when(pautaRepository.findAll()).thenReturn(List.of(pauta));
        when(pautaMapper.toResponse(any())).thenReturn(new PautaResponse(1L, "T", "D", null));

        List<PautaResponse> resultado = pautaService.buscarPautas();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve atualizar o resultado da pauta com sucesso")
    void atualizarResultadoSucesso() {
        Long pautaId = 1L;
        Pauta pauta = new Pauta();
        pauta.setId(pautaId);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));

        pautaService.atualizarResultado(pautaId, ResultadoPauta.APROVADO);

        assertEquals(ResultadoPauta.APROVADO, pauta.getResultado());
        verify(pautaRepository).save(pauta);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar resultado de pauta inexistente")
    void atualizarResultadoErroPautaNaoEncontrada() {
        when(pautaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                pautaService.atualizarResultado(1L, ResultadoPauta.APROVADO)
        );

        verify(pautaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar pauta por ID com sucesso")
    void buscarPautaPorIdSucesso() {
        Pauta pauta = new Pauta();
        pauta.setId(1L);

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));

        Pauta resultado = pautaService.buscarPautaPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(pautaRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando pauta não for encontrada")
    void buscarPautaPorIdErroNaoEncontrado() {
        when(pautaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> pautaService.buscarPautaPorId(99L));

        verify(pautaRepository).findById(99L);
    }
}

package com.desafio.votacao.service;

import com.desafio.votacao.dto.VotacaoRequest;
import com.desafio.votacao.dto.VotacaoResponse;
import com.desafio.votacao.enums.ResultadoPauta;
import com.desafio.votacao.mapper.VotacaoMapper;
import com.desafio.votacao.model.Pauta;
import com.desafio.votacao.model.Votacao;
import com.desafio.votacao.repository.VotacaoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VotacaoServiceTest {
    @InjectMocks
    private VotacaoService votacaoService;

    @Mock
    private VotacaoRepository votacaoRepository;
    @Mock
    private VotacaoMapper votacaoMapper;
    @Mock
    private PautaService pautaService;
    @Mock
    private VotoService votoService;

    @Test
    @DisplayName("Deve criar votação com sucesso")
    void criarVotacaoSucesso() {
        VotacaoRequest dto = new VotacaoRequest(1L, LocalDateTime.now(), 10L);
        Pauta pauta = new Pauta();
        pauta.setId(1L);

        when(pautaService.buscarPautaPorId(1L)).thenReturn(pauta);

        when(votacaoRepository.existsByPautaId(1L)).thenReturn(false);
        when(votacaoRepository.save(any(Votacao.class))).thenAnswer(i -> i.getArgument(0));
        when(votacaoMapper.toResponse(any())).thenReturn(new VotacaoResponse(1L, 1L, null,
                null, false));

        VotacaoResponse result = votacaoService.criarVotacao(dto);

        assertNotNull(result);
        verify(votacaoRepository).save(any(Votacao.class));
        verify(pautaService).buscarPautaPorId(1L);
    }

    @Test
    @DisplayName("Deve falhar ao criar votação para pauta que já possui votação")
    void criarVotacaoErroDuplicado() {
        VotacaoRequest dto = new VotacaoRequest(1L, null, null);
        Pauta pauta = new Pauta();
        pauta.setId(1L);

        when(pautaService.buscarPautaPorId(1L)).thenReturn(pauta);

        when(votacaoRepository.existsByPautaId(1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> votacaoService.criarVotacao(dto));
        verify(votacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve falhar se a pauta não existir (Erro vindo do PautaService)")
    void criarVotacaoErroPautaInexistente() {
        VotacaoRequest dto = new VotacaoRequest(99L, null, null);

        when(pautaService.buscarPautaPorId(99L))
                .thenThrow(new EntityNotFoundException("Pauta não encontrada"));

        assertThrows(EntityNotFoundException.class, () -> votacaoService.criarVotacao(dto));

        verify(votacaoRepository, never()).existsByPautaId(any());
    }

    @Test
    @DisplayName("Deve orquestrar o fechamento de votações encerradas")
    void fecharVotacoesEncerradasSucesso() {
        Votacao v1 = new Votacao();
        v1.setId(100L);
        Pauta p1 = new Pauta();
        p1.setId(1L);
        v1.setPauta(p1);

        when(votacaoRepository.findAllVotacoesEncerradasSemResultado(any())).thenReturn(List.of(v1));
        when(votoService.calcularResultado(100L)).thenReturn(ResultadoPauta.APROVADO);

        votacaoService.fecharVotacoesEncerradas();

        verify(votoService).calcularResultado(100L);
        verify(pautaService).atualizarResultado(1L, ResultadoPauta.APROVADO);
    }

    @Test
    @DisplayName("Deve retornar lista de votações mapeadas")
    void buscarVotacoesSucesso() {
        Votacao v1 = new Votacao(); v1.setId(1L);
        Votacao v2 = new Votacao(); v2.setId(2L);
        List<Votacao> listaEntidades = List.of(v1, v2);

        VotacaoResponse r1 = new VotacaoResponse(1L, 10L, null, null, false);
        VotacaoResponse r2 = new VotacaoResponse(2L, 20L, null, null, true);

        when(votacaoRepository.findAll()).thenReturn(listaEntidades);

        when(votacaoMapper.toResponse(v1)).thenReturn(r1);
        when(votacaoMapper.toResponse(v2)).thenReturn(r2);

        List<VotacaoResponse> resultado = votacaoService.buscarVotacoes();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).id());
        assertEquals(2L, resultado.get(1).id());

        verify(votacaoRepository).findAll();
        verify(votacaoMapper, times(2)).toResponse(any());
    }

    @Test
    @DisplayName("Deve continuar o processamento das demais votações mesmo quando uma falha")
    void deveContinuarProcessamentoQuandoUmaVotacaoFalha() {
        Votacao v1 = new Votacao();
        v1.setId(1L);
        v1.setPauta(new Pauta());
        v1.getPauta().setId(10L);

        Votacao v2 = new Votacao();
        v2.setId(2L);
        v2.setPauta(new Pauta());
        v2.getPauta().setId(20L);

        List<Votacao> expiradas = List.of(v1, v2);

        when(votacaoRepository.findAllVotacoesEncerradasSemResultado(any())).thenReturn(expiradas);

        when(votoService.calcularResultado(1L)).thenThrow(new RuntimeException("Erro catastrófico na v1"));
        when(votoService.calcularResultado(2L)).thenReturn(ResultadoPauta.APROVADO);

        assertDoesNotThrow(() -> votacaoService.fecharVotacoesEncerradas());

        verify(votoService).calcularResultado(1L);
        verify(votoService).calcularResultado(2L);

        verify(pautaService, never()).atualizarResultado(eq(10L), any());

        verify(pautaService).atualizarResultado(20L, ResultadoPauta.APROVADO);
    }
}

package com.desafio.votacao.service;

import com.desafio.votacao.dto.VotoRequest;
import com.desafio.votacao.enums.ResultadoPauta;
import com.desafio.votacao.enums.VotoValor;
import com.desafio.votacao.mapper.VotoMapper;
import com.desafio.votacao.model.Associado;
import com.desafio.votacao.model.Votacao;
import com.desafio.votacao.model.Voto;
import com.desafio.votacao.repository.AssociadoRepository;
import com.desafio.votacao.repository.VotacaoRepository;
import com.desafio.votacao.repository.VotoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VotoServiceTest {
    @InjectMocks
    private VotoService votoService;

    @Mock
    private VotacaoRepository votacaoRepository;
    @Mock
    private VotoRepository votoRepository;
    @Mock
    private AssociadoRepository associadoRepository;
    @Mock
    private VotoMapper votoMapper;


    @Test
    @DisplayName("Deve votar com sucesso")
    void votarSucesso() {
        Long votacaoId = 1L;
        Long associadoId = 1L;
        VotoRequest request = new VotoRequest(votacaoId, associadoId, VotoValor.SIM);

        Votacao votacao = criarVotacao(true);
        votacao.setId(votacaoId);

        Associado associado = new Associado();
        associado.setId(associadoId);

        when(votacaoRepository.findById(votacaoId)).thenReturn(Optional.of(votacao));
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));

        when(votoRepository.existsByVotacaoIdAndAssociadoId(votacaoId, associadoId)).thenReturn(false);

        when(votoMapper.toEntity(any(), any(), any())).thenReturn(new Voto());

        votoService.votar(request);

        verify(votoRepository).save(any(Voto.class));
    }

    @Test
    @DisplayName("Deve falhar quando votação não existe")
    void votarVotacaoInexistente() {
        when(votacaoRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> votoService.votar(new VotoRequest(1L, 1L, VotoValor.SIM)));
    }

    @Test
    @DisplayName("Deve falhar quando votação está fechada")
    void votarVotacaoFechada() {
        Votacao fechada = criarVotacao(false);
        when(votacaoRepository.findById(any())).thenReturn(Optional.of(fechada));

        assertThrows(IllegalStateException.class, () -> votoService.votar(new VotoRequest(1L, 1L, VotoValor.SIM)));
    }

    @Test
    @DisplayName("Deve falhar quando associado já votou")
    void votarDuplicado() {
        Votacao aberta = criarVotacao(true);
        when(votacaoRepository.findById(any())).thenReturn(Optional.of(aberta));
        when(associadoRepository.findById(any())).thenReturn(Optional.of(new Associado()));
        when(votoRepository.existsByVotacaoIdAndAssociadoId(any(), any())).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> votoService.votar(new VotoRequest(1L, 1L, VotoValor.SIM)));
    }

    @Test
    @DisplayName("Deve retornar APROVADO quando SIM vence")
    void resultadoAprovado() {
        when(votoRepository.countByVotacaoIdAndValor(1L, VotoValor.SIM)).thenReturn(10L);
        when(votoRepository.countByVotacaoIdAndValor(1L, VotoValor.NÃO)).thenReturn(5L);

        ResultadoPauta resultado = votoService.calcularResultado(1L);

        assertEquals(ResultadoPauta.APROVADO, resultado);
    }

    @Test
    @DisplayName("Deve retornar REPROVADO em caso de empate ou perda")
    void resultadoReprovado() {
        when(votoRepository.countByVotacaoIdAndValor(1L, VotoValor.SIM)).thenReturn(5L);
        when(votoRepository.countByVotacaoIdAndValor(1L, VotoValor.NÃO)).thenReturn(5L);

        ResultadoPauta resultado = votoService.calcularResultado(1L);

        assertEquals(ResultadoPauta.REPROVADO, resultado);
    }

    private Votacao criarVotacao(boolean aberta) {
        Votacao v = new Votacao();
        if (aberta) {
            v.setDataInicio(LocalDateTime.now().minusMinutes(5));
            v.setDataFim(LocalDateTime.now().plusMinutes(5));
        } else {
            v.setDataInicio(LocalDateTime.now().minusMinutes(10));
            v.setDataFim(LocalDateTime.now().minusMinutes(5));
        }
        return v;
    }
}

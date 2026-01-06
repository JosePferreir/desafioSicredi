package com.desafio.votacao.repository;

import com.desafio.votacao.model.Votacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface VotacaoRepository extends JpaRepository<Votacao, Long> {
    boolean existsByPautaId(Long pautaId);

    @Query("SELECT v FROM Votacao v WHERE v.dataFim <= :agora AND v.pauta.resultado IS NULL")
    List<Votacao> findAllVotacoesEncerradasSemResultado(LocalDateTime agora);
}

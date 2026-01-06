package com.desafio.votacao.repository;

import com.desafio.votacao.enums.VotoValor;
import com.desafio.votacao.model.Voto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotoRepository extends JpaRepository<Voto, Long> {
    long countByVotacaoIdAndValor(Long votacaoId, VotoValor valor);
    boolean existsByVotacaoIdAndAssociadoId(Long votacaoId, Long associadoId);
}

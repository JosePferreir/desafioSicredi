package com.desafio.votacao.repository;

import com.desafio.votacao.model.Associado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssociadoRepository extends JpaRepository<Associado, Long> {
    boolean existsByCpf(String cpf);
}

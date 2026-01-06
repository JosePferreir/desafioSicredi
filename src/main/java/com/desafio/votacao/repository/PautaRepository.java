package com.desafio.votacao.repository;

import com.desafio.votacao.model.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PautaRepository extends JpaRepository<Pauta,Long> {
}

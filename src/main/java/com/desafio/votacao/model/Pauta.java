package com.desafio.votacao.model;

import com.desafio.votacao.enums.ResultadoPauta;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pauta")
public class Pauta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    private ResultadoPauta resultado;

    @OneToOne(mappedBy = "pauta", cascade = CascadeType.ALL)
    private Votacao votacao;
}

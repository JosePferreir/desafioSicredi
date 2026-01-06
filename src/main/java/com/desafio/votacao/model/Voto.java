package com.desafio.votacao.model;

import com.desafio.votacao.enums.VotoValor;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(
        name = "voto",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "fk_voto_votacao_associado",
                        columnNames = {"votacao_id", "associado_id"}
                )
        }
)
public class Voto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "votacao_id", nullable = false)
    private Votacao votacao;

    @ManyToOne
    @JoinColumn(name = "associado_id", nullable = false)
    private Associado associado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VotoValor valor;
}

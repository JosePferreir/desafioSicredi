package com.desafio.votacao.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "votacao")
public class Votacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pauta_id", nullable = false, unique = true)
    private Pauta pauta;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDateTime dataFim;

    public boolean isAberta() {
        LocalDateTime agora = LocalDateTime.now();
        return agora.isAfter(dataInicio) && agora.isBefore(dataFim);
    }

    public static Votacao criar(Pauta p, Long minutos, LocalDateTime inicioRequest){
        LocalDateTime inicio = (inicioRequest != null)
                ? inicioRequest
                : LocalDateTime.now();

        Long duracao = minutos != null ? minutos : 1L;

        Votacao v = new Votacao();
        v.setPauta(p);
        v.setDataInicio(inicio);
        v.setDataFim(inicio.plusMinutes(duracao));
        return v;
    }
}

package com.desafio.votacao.mapper;

import com.desafio.votacao.dto.PautaRequest;
import com.desafio.votacao.dto.PautaResponse;
import com.desafio.votacao.model.Pauta;
import org.springframework.stereotype.Component;

@Component
public class PautaMapper {
    public Pauta toEntity(PautaRequest dto){
        Pauta p = new Pauta();
        p.setDescricao(dto.descricao());
        p.setTitulo(dto.titulo());
        return p;
    }

    public PautaResponse toResponse(Pauta p) {
        return new PautaResponse(
                p.getId(),
                p.getTitulo(),
                p.getDescricao(),
                p.getResultado()
        );
    }
}

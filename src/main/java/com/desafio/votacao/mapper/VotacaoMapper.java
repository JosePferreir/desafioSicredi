package com.desafio.votacao.mapper;

import com.desafio.votacao.dto.VotacaoResponse;
import com.desafio.votacao.model.Votacao;
import org.springframework.stereotype.Component;

@Component
public class VotacaoMapper {
    public VotacaoResponse toResponse(Votacao v){
        return new VotacaoResponse(
                v.getId(),
                v.getPauta().getId(),
                v.getDataInicio(),
                v.getDataFim(),
                v.isAberta()
        );
    }
}

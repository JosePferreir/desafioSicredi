package com.desafio.votacao.mapper;

import com.desafio.votacao.dto.VotoRequest;
import com.desafio.votacao.dto.VotoResponse;
import com.desafio.votacao.model.Associado;
import com.desafio.votacao.model.Votacao;
import com.desafio.votacao.model.Voto;
import org.springframework.stereotype.Component;

@Component
public class VotoMapper {
    public Voto toEntity(VotoRequest request, Votacao votacao, Associado associado) {
        Voto voto = new Voto();
        voto.setValor(request.valor());
        voto.setVotacao(votacao);
        voto.setAssociado(associado);
        return voto;
    }

    public VotoResponse toResponse(Voto entity) {
        return new VotoResponse(
                entity.getId(),
                entity.getVotacao().getId(),
                entity.getAssociado().getId(),
                entity.getValor()
        );
    }
}

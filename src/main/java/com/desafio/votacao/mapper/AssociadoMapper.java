package com.desafio.votacao.mapper;

import com.desafio.votacao.dto.AssociadoRequest;
import com.desafio.votacao.dto.AssociadoResponse;
import com.desafio.votacao.model.Associado;
import org.springframework.stereotype.Component;

@Component
public class AssociadoMapper {
    public Associado toEntity(AssociadoRequest dto){
        Associado a = new Associado();
        a.setNome(dto.nome());
        a.setCpf(dto.cpf());
        return a;
    }

    public AssociadoResponse toResponse(Associado a){
        return new AssociadoResponse(
                a.getId(),
                a.getNome(),
                a.getCpf()
        );
    }
}

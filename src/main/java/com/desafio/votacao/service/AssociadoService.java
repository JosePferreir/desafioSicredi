package com.desafio.votacao.service;

import com.desafio.votacao.dto.AssociadoRequest;
import com.desafio.votacao.dto.AssociadoResponse;
import com.desafio.votacao.mapper.AssociadoMapper;
import com.desafio.votacao.model.Associado;
import com.desafio.votacao.repository.AssociadoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AssociadoService {

    private final AssociadoRepository associadoRepository;
    private final AssociadoMapper associadoMapper;

    public AssociadoService(AssociadoRepository associadoRepository, AssociadoMapper associadoMapper) {
        this.associadoRepository = associadoRepository;
        this.associadoMapper = associadoMapper;
    }

    public AssociadoResponse criarAssociado(AssociadoRequest dto){
        log.info("Iniciando cadastro de novo associado");

        if (associadoRepository.existsByCpf(dto.cpf())) {
            throw new IllegalStateException("Já existe um associado cadastrado com este CPF.");
        }

        Associado a = associadoMapper.toEntity(dto);
        Associado salvo = associadoRepository.save(a);

        log.info("Associado cadastrado com sucesso. ID: {}", salvo.getId());

        return associadoMapper.toResponse(salvo);
    }

    public List<AssociadoResponse> buscarAssociados(){
        return associadoRepository.findAll()
                .stream()
                .map(associadoMapper::toResponse)
                .toList();
    }
}

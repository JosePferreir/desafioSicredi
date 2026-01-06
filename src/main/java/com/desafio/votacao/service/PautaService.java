package com.desafio.votacao.service;

import com.desafio.votacao.dto.PautaRequest;
import com.desafio.votacao.dto.PautaResponse;
import com.desafio.votacao.enums.ResultadoPauta;
import com.desafio.votacao.mapper.PautaMapper;
import com.desafio.votacao.model.Pauta;
import com.desafio.votacao.repository.PautaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PautaService {

    private final PautaRepository pautaRepository;
    private final PautaMapper pautaMapper;

    public PautaService(PautaRepository pautaRepository, PautaMapper pautaMapper) {
        this.pautaRepository = pautaRepository;
        this.pautaMapper = pautaMapper;
    }

    public PautaResponse criarPauta(PautaRequest dto){
        log.info("Criando nova pauta: {}", dto.titulo());

        Pauta p = pautaMapper.toEntity(dto);
        Pauta salva = pautaRepository.save(p);

        log.info("Pauta criada com sucesso. ID: {}", salva.getId());

        return pautaMapper.toResponse(salva);
    }

    public List<PautaResponse> buscarPautas(){
        return pautaRepository.findAll()
                .stream()
                .map(pautaMapper::toResponse)
                .toList();
    }

    public Pauta buscarPautaPorId(Long id){
        return pautaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pauta não encontrada"));
    }

    public void atualizarResultado(Long pautaId, ResultadoPauta resultado) {
        log.info("Atualizando resultado da pauta ID: {} para {}", pautaId, resultado);

        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new EntityNotFoundException("Pauta não encontrada"));

        pauta.setResultado(resultado);
        pautaRepository.save(pauta);

        log.info("Resultado da pauta ID: {} atualizado com sucesso.", pautaId);
    }


}

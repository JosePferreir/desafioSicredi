package com.desafio.votacao.service;

import com.desafio.votacao.dto.VotoRequest;
import com.desafio.votacao.dto.VotoResponse;
import com.desafio.votacao.enums.ResultadoPauta;
import com.desafio.votacao.enums.VotoValor;
import com.desafio.votacao.mapper.VotoMapper;
import com.desafio.votacao.model.Associado;
import com.desafio.votacao.model.Votacao;
import com.desafio.votacao.model.Voto;
import com.desafio.votacao.repository.AssociadoRepository;
import com.desafio.votacao.repository.VotacaoRepository;
import com.desafio.votacao.repository.VotoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class VotoService {

    private final VotoRepository votoRepository;
    private final VotacaoRepository votacaoRepository;
    private final AssociadoRepository associadoRepository;
    private final VotoMapper votoMapper;

    public VotoService(VotoRepository votoRepository,
                       VotacaoRepository votacaoRepository,
                       AssociadoRepository associadoRepository,
                       VotoMapper votoMapper) {
        this.votoRepository = votoRepository;
        this.votacaoRepository = votacaoRepository;
        this.associadoRepository = associadoRepository;
        this.votoMapper = votoMapper;
    }

    /**
     * Registra o voto de um associado em uma sessão de votação.
     * <p>
     * O método executa as seguintes validações antes de persistir o voto:
     * <ol>
     * <li>Verifica se a votação existe e se está aberta (dentro do prazo).</li>
     * <li>Verifica se o associado existe na base local.</li>
     * <li>Consulta se o associado já depositou um voto nesta mesma sessão (impede duplicidade).</li>
     * </ol>
     *
     * @param dto Objeto contendo o ID da votação, ID do associado e o valor do voto (SIM/NAO).
     * @return DTO de resposta com os dados do voto confirmado.
     * @throws EntityNotFoundException Se a votação ou associado não forem encontrados.
     * @throws IllegalStateException Se a votação já estiver encerrada ou o associado já tiver votado.
     */
    @Transactional
    public VotoResponse votar(VotoRequest dto) {
        log.info("Recebendo voto: Associado {} na Pauta {}", dto.associadoId(), dto.votacaoId());

        Votacao votacao = votacaoRepository.findById(dto.votacaoId())
                .orElseThrow(() -> new EntityNotFoundException("Votação não encontrada"));

        if (!votacao.isAberta()) {
            throw new IllegalStateException("Esta votação não está aberta no momento.");
        }

        Associado associado = associadoRepository.findById(dto.associadoId())
                .orElseThrow(() -> new EntityNotFoundException("Associado não encontrado"));

        if (votoRepository.existsByVotacaoIdAndAssociadoId(votacao.getId(), associado.getId())) {
            throw new IllegalStateException("Associado já votou nesta pauta.");
        }

        Voto vt = votoMapper.toEntity(dto, votacao, associado);
        Voto salvo = votoRepository.save(vt);

        log.info("Voto {} registrado com sucesso.", salvo.getId());
        return votoMapper.toResponse(salvo);
    }

    public ResultadoPauta calcularResultado(Long votacaoId) {
        long votosSim = votoRepository.countByVotacaoIdAndValor(votacaoId, VotoValor.SIM);
        long votosNao = votoRepository.countByVotacaoIdAndValor(votacaoId, VotoValor.NÃO);

        if (votosSim > votosNao) {
            return ResultadoPauta.APROVADO;
        }
        return ResultadoPauta.REPROVADO;
    }
}

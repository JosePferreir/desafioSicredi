package com.desafio.votacao.service;

import com.desafio.votacao.dto.VotacaoRequest;
import com.desafio.votacao.dto.VotacaoResponse;
import com.desafio.votacao.enums.ResultadoPauta;
import com.desafio.votacao.mapper.VotacaoMapper;
import com.desafio.votacao.model.Pauta;
import com.desafio.votacao.model.Votacao;
import com.desafio.votacao.repository.PautaRepository;
import com.desafio.votacao.repository.VotacaoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class VotacaoService {

    private final VotacaoRepository votacaoRepository;
    private final VotacaoMapper votacaoMapper;
    private final VotoService votoService;
    private final PautaService pautaService;

    public VotacaoService(VotacaoRepository votacaoRepository,
                          VotacaoMapper votacaoMapper,
                          PautaRepository pautaRepository,
                          VotoService votoService,
                          PautaService pautaService) {
        this.votacaoRepository = votacaoRepository;
        this.votacaoMapper = votacaoMapper;
        this.votoService = votoService;
        this.pautaService = pautaService;
    }

    public VotacaoResponse criarVotacao(VotacaoRequest dto){
        Pauta p = pautaService.buscarPautaPorId(dto.pautaId());
        log.info("Solicitação para criar votação na pauta ID: {}", dto.pautaId());

        if (votacaoRepository.existsByPautaId(p.getId())) {
            throw new IllegalStateException("Já existe uma votação criada para esta pauta.");
        }

        Votacao v = Votacao.criar(p, dto.tempoEmMinutos(), dto.dataInicio());
        Votacao salva = votacaoRepository.save(v);

        log.info("Votação ID: {} criada com sucesso para pauta ID: {}. Expira em: {}",
                salva.getId(), p.getId(), salva.getDataFim());

        return votacaoMapper.toResponse(salva);
    }

    public List<VotacaoResponse> buscarVotacoes(){
        return votacaoRepository.findAll()
                .stream()
                .map(votacaoMapper::toResponse)
                .toList();
    }

    /**
     * Busca todas as votações expiradas e orquestra o encerramento de cada uma.
     * <p>
     * Este método busca todas as votações que:
     * <ul>
     * <li>Possuem data de término anterior ao momento atual.</li>
     * <li>Ainda não tiveram seu resultado processado na pauta.</li>
     */
    public void fecharVotacoesEncerradas() {
        LocalDateTime agora = LocalDateTime.now();

        List<Votacao> expiradas = votacaoRepository.findAllVotacoesEncerradasSemResultado(agora);

        for (Votacao votacao : expiradas) {
            try {
                processarEncerramento(votacao);
                log.info("Votação ID: {} da pauta ID: {} encerrada e processada.",
                        votacao.getId(), votacao.getPauta().getId());
            } catch (Exception e) {
                log.error("Erro ao processar encerramento da votação ID: {}. Causa: {}",
                        votacao.getId(), e.getMessage());
            }
        }
    }

    /**
     * Realiza o cálculo de votos e persistência do resultado de forma atômica.
     */
    @Transactional
    public void processarEncerramento(Votacao votacao) {
        log.debug("Calculando resultado final para votação ID: {}", votacao.getId());
        ResultadoPauta resultado = votoService.calcularResultado(votacao.getId());

        pautaService.atualizarResultado(votacao.getPauta().getId(), resultado);
    }
}

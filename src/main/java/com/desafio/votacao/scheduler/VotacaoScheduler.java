package com.desafio.votacao.scheduler;

import com.desafio.votacao.service.VotacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VotacaoScheduler {
    private final VotacaoService votacaoService;

    /**
     * Job agendado (executado a cada minuto) para disparar o encerramento de votações.
     * <p>
     * Este método atua apenas como um gatilho, a lógica de negócio está no service.
     */
    @Scheduled(fixedDelay = 60000)
    public void verificarVotacoesEncerradas() {
        log.info("Executando job de verificação de votações...");
        votacaoService.fecharVotacoesEncerradas();
    }
}

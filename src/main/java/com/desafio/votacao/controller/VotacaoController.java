package com.desafio.votacao.controller;

import com.desafio.votacao.dto.VotacaoRequest;
import com.desafio.votacao.dto.VotacaoResponse;
import com.desafio.votacao.service.VotacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/votacoes")
@Tag(name = "Votação", description = "Gerenciamento de sessões de votação")
public class VotacaoController {

    private final VotacaoService votacaoService;

    public VotacaoController(VotacaoService votacaoService) {
        this.votacaoService = votacaoService;
    }

    @Operation(
            summary = "Abrir uma nova votação",
            description = "Abre uma sessão de votação para uma pauta específica. Se o tempo não for informado, será de 1 minuto."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Votação criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "409", description = "Já existe uma votação aberta para esta pauta"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    })
    @PostMapping
    public ResponseEntity<VotacaoResponse> criarVotacao(@Valid @RequestBody VotacaoRequest dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(votacaoService.criarVotacao(dto));
    }

    @Operation(
            summary = "Listar sessões de votação",
            description = "Retorna a lista de todas as sessões de votação criadas, indicando se estão abertas ou não."
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<VotacaoResponse>> buscarVotacoes(){
        return ResponseEntity.ok(votacaoService.buscarVotacoes());
    }
}

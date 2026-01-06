package com.desafio.votacao.controller;

import com.desafio.votacao.dto.VotoRequest;
import com.desafio.votacao.dto.VotoResponse;
import com.desafio.votacao.service.VotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/votos")
@Tag(name = "Votos", description = "Registro e processamento de votos nas sessões")
public class VotoController {

    private final VotoService votoService;

    public VotoController(VotoService votoService) {
        this.votoService = votoService;
    }

    @Operation(
            summary = "Registrar um voto",
            description = "Computa o voto de um associado em uma sessão de votação específica. " +
                    "Verifica se a sessão está aberta, se o associado já votou e se está apto."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou formato incorreto"),
            @ApiResponse(responseCode = "404", description = "Votação ou Associado não encontrados"),
            @ApiResponse(responseCode = "409", description = "Conflito: Associado já votou nesta sessão ou a sessão já está encerrada")
    })
    @PostMapping
    public ResponseEntity<VotoResponse> votas(@Valid @RequestBody VotoRequest dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(votoService.votar(dto));
    }
}

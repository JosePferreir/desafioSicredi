package com.desafio.votacao.controller;

import com.desafio.votacao.dto.PautaRequest;
import com.desafio.votacao.dto.PautaResponse;
import com.desafio.votacao.service.PautaService;
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
@RequestMapping("/pautas")
@Tag(name = "Pautas", description = "Gerenciamento de assuntos (pautas) para votação")
public class PautaController {

    private final PautaService pautaService;

    public PautaController(PautaService pautaService) {
        this.pautaService = pautaService;
    }

    @Operation(
            summary = "Cadastrar nova pauta",
            description = "Cria uma nova pauta contendo título e descrição para ser submetida a votação em uma sessão futura."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pauta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (título ou descrição ausentes)")
    })
    @PostMapping
    public ResponseEntity<PautaResponse> criarPauta(@Valid @RequestBody PautaRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pautaService.criarPauta(dto));
    }

    @Operation(
            summary = "Listar pautas",
            description = "Retorna a lista de todas as pautas cadastradas e seus resultados (se já houver)."
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<PautaResponse>> buscarPautas() {
        return ResponseEntity.ok(pautaService.buscarPautas());
    }
}

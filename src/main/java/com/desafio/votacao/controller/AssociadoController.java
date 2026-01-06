package com.desafio.votacao.controller;

import com.desafio.votacao.dto.AssociadoRequest;
import com.desafio.votacao.dto.AssociadoResponse;
import com.desafio.votacao.service.AssociadoService;
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
@RequestMapping("/associados")
@Tag(name = "Associados", description = "Gerenciamento de Associados")
public class AssociadoController {

    private final AssociadoService associadoService;

    public AssociadoController(AssociadoService associadoService) {
        this.associadoService = associadoService;
    }

    @Operation(
            summary = "Cadastrar novo associado",
            description = "Registra um novo associado na base de dados. O CPF deve ser válido e único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Associado criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (CPF inválido ou campos nulos)"),
            @ApiResponse(responseCode = "409", description = "CPF já cadastrado no sistema")
    })
    @PostMapping
    public ResponseEntity<AssociadoResponse> criarAssociado(@Valid @RequestBody AssociadoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(associadoService.criarAssociado(dto));
    }

    @Operation(
            summary = "Listar associados",
            description = "Retorna a lista completa de todos os associados cadastrados."
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<AssociadoResponse>> buscarAssociados(){
        return ResponseEntity.ok(associadoService.buscarAssociados());
    }
}

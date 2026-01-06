package com.desafio.votacao.controller;

import com.desafio.votacao.dto.VotacaoRequest;
import com.desafio.votacao.dto.VotacaoResponse;
import com.desafio.votacao.exceptions.GlobalExceptionHandler;
import com.desafio.votacao.service.VotacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(VotacaoController.class)
@Import(GlobalExceptionHandler.class)
public class VotacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VotacaoService votacaoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve retornar 201 e criar votação com sucesso")
    void criarVotacaoSucesso() throws Exception {
        VotacaoRequest request = new VotacaoRequest(1L, LocalDateTime.now().plusHours(1), 10L);

        VotacaoResponse response = new VotacaoResponse(50L, 1L, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10L), true);

        when(votacaoService.criarVotacao(any(VotacaoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/votacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(50L))
                .andExpect(jsonPath("$.pautaId").value(1L));
    }

    @Test
    @DisplayName("Deve retornar 400 se faltar dados obrigatórios (Validation)")
    void criarVotacaoErroValidacao() throws Exception {
        VotacaoRequest requestInvalido = new VotacaoRequest(null, null, null);

        mockMvc.perform(post("/votacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.pautaId").exists());
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict se já existir votação para a pauta")
    void criarVotacaoErroDuplicada() throws Exception {
        VotacaoRequest request = new VotacaoRequest(1L, LocalDateTime.now().plusHours(1), 1L);

        when(votacaoService.criarVotacao(any(VotacaoRequest.class)))
                .thenThrow(new IllegalStateException("Já existe uma votação criada para esta pauta."));

        mockMvc.perform(post("/votacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Já existe uma votação criada para esta pauta."));
    }

    @Test
    @DisplayName("Deve retornar 404 se a Pauta não existir")
    void criarVotacaoErroPautaInexistente() throws Exception {
        VotacaoRequest request = new VotacaoRequest(99L, LocalDateTime.now().plusHours(1), 1L);

        when(votacaoService.criarVotacao(any(VotacaoRequest.class)))
                .thenThrow(new EntityNotFoundException("Pauta não encontrada"));

        mockMvc.perform(post("/votacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Pauta não encontrada"));
    }

    @Test
    @DisplayName("Deve listar votações com sucesso")
    void buscarVotacoesSucesso() throws Exception {
        List<VotacaoResponse> lista = List.of(
                new VotacaoResponse(1L, 1L, LocalDateTime.now(),
                        LocalDateTime.now().plusMinutes(10L), false),
                new VotacaoResponse(2L, 2L, LocalDateTime.now(),
                        LocalDateTime.now().plusMinutes(10L), true)
        );

        when(votacaoService.buscarVotacoes()).thenReturn(lista);

        mockMvc.perform(get("/votacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }
}

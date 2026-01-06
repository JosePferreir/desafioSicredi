package com.desafio.votacao.controller;

import com.desafio.votacao.dto.PautaRequest;
import com.desafio.votacao.dto.PautaResponse;
import com.desafio.votacao.enums.ResultadoPauta;
import com.desafio.votacao.exceptions.GlobalExceptionHandler;
import com.desafio.votacao.service.PautaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PautaController.class)
@Import(GlobalExceptionHandler.class)
public class PautaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PautaService pautaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve retornar 201 ao criar pauta com sucesso")
    void criarPautaSucesso() throws Exception {
        PautaRequest request = new PautaRequest("Nova Pauta", "Descrição da pauta");
        PautaResponse response = new PautaResponse(1L, "Nova Pauta", "Descrição da pauta", null);

        when(pautaService.criarPauta(any(PautaRequest.class))).thenReturn(response);

        mockMvc.perform(post("/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.titulo").value("Nova Pauta"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando o título da pauta for nulo ou vazio")
    void criarPautaErroValidacao() throws Exception {
        PautaRequest requestInvalido = new PautaRequest("", "Descrição");

        mockMvc.perform(post("/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Erro de validação nos campos"))
                .andExpect(jsonPath("$.validationErrors.titulo").exists());
    }

    @Test
    @DisplayName("Deve retornar 200 ao buscar lista de pautas")
    void buscarPautasSucesso() throws Exception {
        List<PautaResponse> lista = List.of(
                new PautaResponse(1L, "Pauta 1", "D1", null),
                new PautaResponse(2L, "Pauta 2", "D2", ResultadoPauta.APROVADO)
        );

        when(pautaService.buscarPautas()).thenReturn(lista);

        mockMvc.perform(get("/pautas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titulo").value("Pauta 1"))
                .andExpect(jsonPath("$[1].resultado").value("APROVADO"));
    }
}

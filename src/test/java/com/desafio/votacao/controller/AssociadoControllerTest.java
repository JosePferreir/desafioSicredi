package com.desafio.votacao.controller;

import com.desafio.votacao.dto.AssociadoRequest;
import com.desafio.votacao.dto.AssociadoResponse;
import com.desafio.votacao.exceptions.GlobalExceptionHandler;
import com.desafio.votacao.service.AssociadoService;
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

@WebMvcTest(AssociadoController.class)
@Import(GlobalExceptionHandler.class)
public class AssociadoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssociadoService associadoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve retornar 201 ao criar associado com sucesso")
    void criarAssociadoSucesso() throws Exception {
        AssociadoRequest request = new AssociadoRequest("João Silva", "12345678901");
        AssociadoResponse response = new AssociadoResponse(1L, "João Silva", "12345678901");

        when(associadoService.criarAssociado(any(AssociadoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/associados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando o request for inválido (Validação)")
    void criarAssociadoErroValidacao() throws Exception {
        AssociadoRequest requestInvalido = new AssociadoRequest("", "");

        mockMvc.perform(post("/associados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Erro de validação nos campos"))
                .andExpect(jsonPath("$.validationErrors.nome").exists())
                .andExpect(jsonPath("$.validationErrors.cpf").exists());
    }

    @Test
    @DisplayName("Deve retornar 409 quando o service lançar IllegalStateException (CPF Duplicado)")
    void criarAssociadoErroConflito() throws Exception {
        AssociadoRequest request = new AssociadoRequest("João", "12345678901");

        when(associadoService.criarAssociado(any(AssociadoRequest.class)))
                .thenThrow(new IllegalStateException("Já existe um associado cadastrado com este CPF."));

        mockMvc.perform(post("/associados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Já existe um associado cadastrado com este CPF."));
    }

    @Test
    @DisplayName("Deve retornar 200 ao buscar lista de associados")
    void buscarTodosSucesso() throws Exception {
        List<AssociadoResponse> lista = List.of(
                new AssociadoResponse(1L, "João", "111"),
                new AssociadoResponse(2L, "Maria", "222")
        );

        when(associadoService.buscarAssociados()).thenReturn(lista);

        mockMvc.perform(get("/associados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("João"))
                .andExpect(jsonPath("$[1].nome").value("Maria"));
    }
}

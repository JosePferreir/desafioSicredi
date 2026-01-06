package com.desafio.votacao.controller;

import com.desafio.votacao.dto.VotoRequest;
import com.desafio.votacao.dto.VotoResponse;
import com.desafio.votacao.enums.VotoValor;
import com.desafio.votacao.exceptions.GlobalExceptionHandler;
import com.desafio.votacao.service.VotoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(VotoController.class)
@Import(GlobalExceptionHandler.class)
public class VotoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VotoService votoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve retornar 201 e o voto criado quando os dados forem válidos")
    void votarSucesso() throws Exception {
        VotoRequest request = new VotoRequest(1L, 1L, VotoValor.SIM);
        VotoResponse response = new VotoResponse(100L, 1L, 1L, VotoValor.SIM);

        when(votoService.votar(any(VotoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.valor").value("SIM"));
    }

    @Test
    @DisplayName("Deve retornar 409 quando o associado já tiver votado")
    void votarErroDuplicado() throws Exception {
        VotoRequest request = new VotoRequest(1L, 1L, VotoValor.SIM);

        when(votoService.votar(any(VotoRequest.class)))
                .thenThrow(new IllegalStateException("Associado já votou nesta pauta."));

        mockMvc.perform(post("/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Associado já votou nesta pauta."));
    }

    @Test
    @DisplayName("Deve retornar 400 (MethodArgumentNotValidException) quando campos obrigatórios forem nulos")
    void votarErroValidacao() throws Exception {
        VotoRequest requestInvalido = new VotoRequest(null, null, null);

        mockMvc.perform(post("/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Erro de validação nos campos"))
                .andExpect(jsonPath("$.validationErrors.votacaoId").exists())
                .andExpect(jsonPath("$.validationErrors.associadoId").exists());
    }

    @Test
    @DisplayName("Deve retornar 400 (HttpMessageNotReadableException) quando o valor do enum for inválido")
    void votarErroEnumInvalido() throws Exception {
        String jsonInvalido = """
                {
                    "votacaoId": 1,
                    "associadoId": 1,
                    "valor": "TALVEZ"
                }
                """;

        mockMvc.perform(post("/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Erro na leitura do JSON"))
                .andExpect(jsonPath("$.validationErrors.valor").value(containsString("Opções aceitas: [SIM, NÃO]")));
    }
}

package com.desafio.votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PautaRequest (
        @Schema(description = "Título da pauta", example = "Aumento de Salário")
        @NotBlank(message = "O título é obrigatório")
        String titulo,

        @Schema(description = "Detalhamento do assunto",
                example = "Proposta de aumento de 10% para todos os colaboradores")
        String descricao
) {
}

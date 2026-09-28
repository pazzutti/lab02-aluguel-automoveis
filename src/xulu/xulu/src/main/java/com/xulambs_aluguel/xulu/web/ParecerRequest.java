package com.xulambs_aluguel.xulu.web;

import com.xulambs_aluguel.xulu.model.ResultadoParecer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Corpo JSON esperado ao registrar o parecer de um agente sobre um pedido
 * (POST /api/pedidos/{id}/parecer).
 */
public record ParecerRequest(
        @NotNull(message = "Resultado e obrigatorio") ResultadoParecer resultado,
        @NotBlank(message = "Justificativa e obrigatoria") String justificativa
) {
}

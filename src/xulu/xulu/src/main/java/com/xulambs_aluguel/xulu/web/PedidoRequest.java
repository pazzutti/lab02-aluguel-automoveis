package com.xulambs_aluguel.xulu.web;

import com.xulambs_aluguel.xulu.model.ModalidadeContrato;
import jakarta.validation.constraints.NotNull;

/**
 * Corpo JSON esperado na criacao de um pedido (POST /api/pedidos).
 */
public record PedidoRequest(
        @NotNull(message = "Automovel e obrigatorio") Long automovelId,
        @NotNull(message = "Modalidade e obrigatoria") ModalidadeContrato modalidade
) {
}

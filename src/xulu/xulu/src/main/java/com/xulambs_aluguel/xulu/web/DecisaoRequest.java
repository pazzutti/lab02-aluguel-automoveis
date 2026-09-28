package com.xulambs_aluguel.xulu.web;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Corpo JSON esperado na decisao do cliente sobre um pedido avaliado
 * (POST /api/pedidos/{id}/decisao). Os campos alem de "aceitar" so sao
 * exigidos dependendo da modalidade do pedido (checado em ContratoService,
 * nao aqui via Bean Validation, ja que a obrigatoriedade e condicional):
 * prazo/dataPrevistaDevolucao para LOCACAO, valorMensalidade/recorrencia
 * para ASSINATURA; LEASING nao precisa de nenhum.
 */
public record DecisaoRequest(
        boolean aceitar,
        Integer prazo,
        LocalDate dataPrevistaDevolucao,
        BigDecimal valorMensalidade,
        String recorrencia
) {
}

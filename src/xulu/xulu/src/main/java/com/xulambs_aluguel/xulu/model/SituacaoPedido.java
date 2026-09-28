package com.xulambs_aluguel.xulu.model;

/**
 * Fluxo de avaliacao de um Pedido (ver diagrama de classes / RF24): criado
 * como NAO_AVALIADO, o agente move para EM_ANALISE/AVALIADO e o cliente decide
 * ACEITO/RECUSADO; CANCELADO e exclusivo do cliente, e so antes da avaliacao.
 */
public enum SituacaoPedido {
    NAO_AVALIADO,
    EM_ANALISE,
    AVALIADO,
    ACEITO,
    RECUSADO,
    CANCELADO
}

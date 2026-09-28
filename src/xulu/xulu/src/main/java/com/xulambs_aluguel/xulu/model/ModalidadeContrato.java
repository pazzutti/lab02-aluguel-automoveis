package com.xulambs_aluguel.xulu.model;

/**
 * Modalidades de contrato que um Pedido pode indicar (ver diagrama de classes).
 * Locacao/Assinatura/Leasing em si ainda nao sao implementados -- o Pedido so
 * guarda a intencao do cliente.
 */
public enum ModalidadeContrato {
    LOCACAO,
    ASSINATURA,
    LEASING
}

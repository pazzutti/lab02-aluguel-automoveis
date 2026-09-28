package com.xulambs_aluguel.xulu.model;

/**
 * Ciclo de vida de um Contrato (ver diagrama de classes). Leasing nasce
 * PENDENTE ate o banco conceder credito; Locacao/Assinatura nascem direto
 * EM_EXECUCAO. So Locacao tem um caminho definido ate ENCERRADO
 * (registrarDevolucao) -- Assinatura/Leasing nao tem "encerramento" no
 * diagrama.
 */
public enum SituacaoContrato {
    PENDENTE,
    EM_EXECUCAO,
    ENCERRADO
}

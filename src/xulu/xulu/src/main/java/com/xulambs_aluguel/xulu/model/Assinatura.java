package com.xulambs_aluguel.xulu.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

/**
 * Contrato de assinatura: uso recorrente, com mensalidade (ver diagrama de
 * classes). Sem "nullable = false" nas colunas proprias -- tabela
 * compartilhada com Locacao/Leasing (SINGLE_TABLE).
 */
@Entity
@DiscriminatorValue("ASSINATURA")
public class Assinatura extends Contrato {

    @Column(precision = 12, scale = 2)
    private BigDecimal valorMensalidade;

    @Column(length = 30)
    private String recorrencia;

    protected Assinatura() {
    }

    public Assinatura(Pedido pedido, BigDecimal valorMensalidade, String recorrencia) {
        super(pedido, SituacaoContrato.EM_EXECUCAO);
        this.valorMensalidade = valorMensalidade;
        this.recorrencia = recorrencia;
    }

    public BigDecimal getValorMensalidade() {
        return valorMensalidade;
    }

    public String getRecorrencia() {
        return recorrencia;
    }
}

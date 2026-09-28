package com.xulambs_aluguel.xulu.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

/**
 * Contrato de leasing: longo prazo, associado a um credito concedido por um
 * banco (ver diagrama de classes). Nasce PENDENTE ate o banco conceder o
 * credito -- so entao comeca a execucao.
 */
@Entity
@DiscriminatorValue("LEASING")
public class Leasing extends Contrato {

    @OneToOne
    @JoinColumn(name = "contrato_credito_id")
    private ContratoCredito contratoCredito;

    protected Leasing() {
    }

    public Leasing(Pedido pedido) {
        super(pedido, SituacaoContrato.PENDENTE);
    }

    /**
     * HU do Banco (Banco.concederCredito no diagrama): associa o credito e
     * inicia a execucao do leasing.
     */
    public void concederCredito(ContratoCredito credito) {
        if (getSituacao() != SituacaoContrato.PENDENTE) {
            throw new IllegalStateException("Este leasing ja tem credito concedido");
        }
        this.contratoCredito = credito;
        definirDataInicio(java.time.LocalDateTime.now());
        definirSituacao(SituacaoContrato.EM_EXECUCAO);
    }

    public ContratoCredito getContratoCredito() {
        return contratoCredito;
    }
}

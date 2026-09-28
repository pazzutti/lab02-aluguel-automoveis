package com.xulambs_aluguel.xulu.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDate;

/**
 * Contrato de locacao: prazo determinado, com devolucao (ver diagrama de
 * classes). Sem "nullable = false" nas colunas proprias -- tabela
 * compartilhada com Assinatura/Leasing (SINGLE_TABLE).
 */
@Entity
@DiscriminatorValue("LOCACAO")
public class Locacao extends Contrato {

    @Column
    private Integer prazo;

    @Column
    private LocalDate dataPrevistaDevolucao;

    @Column
    private LocalDate dataEfetivaDevolucao;

    protected Locacao() {
    }

    public Locacao(Pedido pedido, int prazo, LocalDate dataPrevistaDevolucao) {
        super(pedido, SituacaoContrato.EM_EXECUCAO);
        this.prazo = prazo;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
    }

    /**
     * HU da Empresa (Empresa.registrarDevolucao no diagrama): encerra a
     * locacao quando o automovel e devolvido.
     */
    public void registrarDevolucao(LocalDate data) {
        if (getSituacao() != SituacaoContrato.EM_EXECUCAO) {
            throw new IllegalStateException("Somente locacoes em execucao podem receber devolucao");
        }
        this.dataEfetivaDevolucao = data;
        definirSituacao(SituacaoContrato.ENCERRADO);
    }

    public Integer getPrazo() {
        return prazo;
    }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public LocalDate getDataEfetivaDevolucao() {
        return dataEfetivaDevolucao;
    }
}

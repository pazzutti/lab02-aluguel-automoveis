package com.xulambs_aluguel.xulu.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Credito concedido por um Banco para viabilizar um Leasing (ver diagrama de
 * classes).
 */
@Entity
@Table(name = "contrato_credito")
public class ContratoCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDate dataConcessao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "banco_id")
    private Banco banco;

    protected ContratoCredito() {
    }

    public ContratoCredito(BigDecimal valor, Banco banco) {
        this.valor = valor;
        this.banco = banco;
        this.dataConcessao = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDate getDataConcessao() {
        return dataConcessao;
    }

    public Banco getBanco() {
        return banco;
    }
}

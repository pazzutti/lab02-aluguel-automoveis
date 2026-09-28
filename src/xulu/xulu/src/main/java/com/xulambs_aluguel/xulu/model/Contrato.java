package com.xulambs_aluguel.xulu.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Contrato originado da aceitacao de um Pedido (ver diagrama de classes).
 * Uma unica tabela "contrato" guarda Locacao/Assinatura/Leasing
 * (SINGLE_TABLE), igual ao padrao ja usado em Usuario: colunas especificas de
 * cada subtipo ficam NULL nas linhas dos outros.
 */
@Entity
@Table(name = "contrato")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_contrato")
public abstract class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataInicio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SituacaoContrato situacao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "automovel_id")
    private Automovel automovel;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToOne(optional = false)
    @JoinColumn(name = "pedido_id", unique = true)
    private Pedido pedido;

    protected Contrato() {
    }

    protected Contrato(Pedido pedido, SituacaoContrato situacaoInicial) {
        this.pedido = pedido;
        this.automovel = pedido.getAutomovel();
        this.cliente = pedido.getCliente();
        this.situacao = situacaoInicial;
        this.dataInicio = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public SituacaoContrato getSituacao() {
        return situacao;
    }

    public Automovel getAutomovel() {
        return automovel;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Pedido getPedido() {
        return pedido;
    }

    protected void definirDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    protected void definirSituacao(SituacaoContrato situacao) {
        this.situacao = situacao;
    }
}

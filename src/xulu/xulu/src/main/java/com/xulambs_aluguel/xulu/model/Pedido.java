package com.xulambs_aluguel.xulu.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Pedido de aluguel feito por um Cliente (ver diagrama de classes / RF24).
 * Nasce NAO_AVALIADO; o avanco pelas demais situacoes (analise do agente,
 * decisao do cliente) ainda nao tem endpoint proprio.
 */
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "automovel_id")
    private Automovel automovel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModalidadeContrato modalidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SituacaoPedido situacao;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    private LocalDateTime dataAlteracao;

    protected Pedido() {
    }

    public Pedido(Cliente cliente, Automovel automovel, ModalidadeContrato modalidade) {
        this.cliente = cliente;
        this.automovel = automovel;
        this.modalidade = modalidade;
        this.situacao = SituacaoPedido.NAO_AVALIADO;
        this.dataCriacao = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Automovel getAutomovel() {
        return automovel;
    }

    public ModalidadeContrato getModalidade() {
        return modalidade;
    }

    public SituacaoPedido getSituacao() {
        return situacao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataAlteracao() {
        return dataAlteracao;
    }
}

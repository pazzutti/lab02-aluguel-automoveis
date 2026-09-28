package com.xulambs_aluguel.xulu.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Pedido de aluguel feito por um Cliente (ver diagrama de classes / RF24).
 * Nasce NAO_AVALIADO e percorre a avaliacao do agente ate ser aceito,
 * recusado ou cancelado pelo cliente.
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

    /**
     * Lado inverso da relacao (o FK "pedido_id" mora em Parecer, que e quem
     * grava o registro) -- so leitura, para o PedidoResponse expor o parecer
     * sem precisar de outra consulta.
     */
    @OneToOne(mappedBy = "pedido", fetch = FetchType.EAGER)
    private Parecer parecer;

    protected Pedido() {
    }

    public Pedido(Cliente cliente, Automovel automovel, ModalidadeContrato modalidade) {
        this.cliente = cliente;
        this.automovel = automovel;
        this.modalidade = modalidade;
        this.situacao = SituacaoPedido.NAO_AVALIADO;
        this.dataCriacao = LocalDateTime.now();
    }

    /**
     * HU do Agente (Agente.analisarPedido no diagrama): marca que um agente
     * comecou a analisar o pedido.
     */
    public void iniciarAnalise() {
        exigirSituacao(SituacaoPedido.NAO_AVALIADO);
        this.situacao = SituacaoPedido.EM_ANALISE;
        marcarAlterado();
    }

    /**
     * HU do Agente (Agente.registrarParecer no diagrama): parecer favoravel
     * aguarda a decisao do cliente (AVALIADO); parecer desfavoravel encerra o
     * pedido direto (RF: "se parecer positivo, encaminhado ao cliente").
     */
    public void avaliar(ResultadoParecer resultado) {
        exigirSituacao(SituacaoPedido.EM_ANALISE);
        this.situacao = resultado == ResultadoParecer.FAVORAVEL ? SituacaoPedido.AVALIADO : SituacaoPedido.RECUSADO;
        marcarAlterado();
    }

    /**
     * HU do Cliente: decide se avanca para a execucao do contrato depois de
     * um parecer favoravel.
     */
    public void decidir(boolean aceitar) {
        exigirSituacao(SituacaoPedido.AVALIADO);
        this.situacao = aceitar ? SituacaoPedido.ACEITO : SituacaoPedido.RECUSADO;
        marcarAlterado();
    }

    /**
     * HU do Cliente: cancela o pedido, so permitido enquanto nao houver
     * parecer registrado.
     */
    public void cancelar() {
        exigirNaoAvaliado();
        this.situacao = SituacaoPedido.CANCELADO;
        marcarAlterado();
    }

    /**
     * HU do Cliente (Pedido.alterar no diagrama): troca automovel/modalidade,
     * so permitido enquanto nao houver parecer registrado.
     */
    public void alterar(Automovel automovel, ModalidadeContrato modalidade) {
        exigirNaoAvaliado();
        this.automovel = automovel;
        this.modalidade = modalidade;
        marcarAlterado();
    }

    private void exigirSituacao(SituacaoPedido esperada) {
        if (this.situacao != esperada) {
            throw new IllegalStateException("Pedido precisa estar " + esperada + " (esta " + this.situacao + ")");
        }
    }

    private void exigirNaoAvaliado() {
        if (this.situacao != SituacaoPedido.NAO_AVALIADO && this.situacao != SituacaoPedido.EM_ANALISE) {
            throw new IllegalStateException("Pedido ja foi avaliado, nao pode mais ser alterado/cancelado");
        }
    }

    private void marcarAlterado() {
        this.dataAlteracao = LocalDateTime.now();
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

    public Parecer getParecer() {
        return parecer;
    }
}

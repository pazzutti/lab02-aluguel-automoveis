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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Parecer emitido por um Agente sobre um Pedido (ver diagrama de classes).
 * Um pedido recebe no maximo um parecer (Pedido "1" -- "0..1" Parecer);
 * imutavel depois de criado.
 */
@Entity
@Table(name = "parecer")
public class Parecer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "pedido_id", unique = true)
    private Pedido pedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "agente_id")
    private Agente agente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResultadoParecer resultado;

    @Column(nullable = false, length = 500)
    private String justificativa;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    protected Parecer() {
    }

    public Parecer(Pedido pedido, Agente agente, ResultadoParecer resultado, String justificativa) {
        this.pedido = pedido;
        this.agente = agente;
        this.resultado = resultado;
        this.justificativa = justificativa;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public Agente getAgente() {
        return agente;
    }

    public ResultadoParecer getResultado() {
        return resultado;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}

package com.xulambs_aluguel.xulu.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Automovel disponivel para aluguel (ver diagrama de classes). Cadastro por
 * uma Empresa (Empresa.cadastrarAutomovel()) ainda nao tem endpoint proprio --
 * por enquanto os automoveis vem do DataSeeder.
 */
@Entity
@Table(name = "automovel")
public class Automovel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String placa;

    @Column(nullable = false)
    private int ano;

    @Column(nullable = false, length = 60)
    private String marca;

    @Column(nullable = false, length = 60)
    private String modelo;

    @Column(nullable = false)
    private boolean disponivel = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "proprietario_id")
    private Usuario proprietario;

    protected Automovel() {
    }

    public Automovel(String placa, int ano, String marca, String modelo, Usuario proprietario) {
        this.placa = placa;
        this.ano = ano;
        this.marca = marca;
        this.modelo = modelo;
        this.proprietario = proprietario;
    }

    public Long getId() {
        return id;
    }

    public String getPlaca() {
        return placa;
    }

    public int getAno() {
        return ano;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    /**
     * Chamado ao criar um Contrato a partir de um Pedido aceito -- o automovel
     * fica reservado mesmo enquanto um leasing ainda esta PENDENTE de credito.
     */
    public void marcarIndisponivel() {
        this.disponivel = false;
    }

    /**
     * Chamado por Empresa.registrarDevolucao() (via Locacao) quando o
     * automovel volta para a frota.
     */
    public void marcarDisponivel() {
        this.disponivel = true;
    }

    public Usuario getProprietario() {
        return proprietario;
    }
}

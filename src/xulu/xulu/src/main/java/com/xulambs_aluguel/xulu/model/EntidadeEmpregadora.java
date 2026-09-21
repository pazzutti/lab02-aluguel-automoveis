package com.xulambs_aluguel.xulu.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

/**
 * Entidade empregadora de um cliente, com o rendimento que ele aufere nela.
 *
 * No diagrama de classes a relacao com Cliente e uma COMPOSICAO
 * (Cliente "1" *-- "0..n" EntidadeEmpregadora): a empregadora nao existe
 * sozinha, so faz sentido dentro de um cliente. Por isso ela e @Embeddable
 * e nao @Entity -- nao tem id proprio nem tabela propria, e o Hibernate
 * grava/apaga junto com o cliente dono.
 */
@Embeddable
public class EntidadeEmpregadora {

    private String nome;

    @Column(precision = 12, scale = 2)
    private BigDecimal rendimento;

    /**
     * O JPA exige um construtor sem argumentos para conseguir instanciar
     * o objeto ao ler do banco. Nao remova.
     */
    protected EntidadeEmpregadora() {
    }

    public EntidadeEmpregadora(String nome, BigDecimal rendimento) {
        this.nome = nome;
        this.rendimento = rendimento;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getRendimento() {
        return rendimento;
    }
}

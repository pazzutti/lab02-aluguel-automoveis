package com.xulambs_aluguel.xulu.model;

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

    // TODO-1: declare os dois atributos do diagrama:
    //   - nome (String)
    //   - rendimento (BigDecimal)  <- use BigDecimal, nunca double, para dinheiro
    // Dica: anote a coluna do rendimento com
    //   @Column(precision = 12, scale = 2)
    // para o MySQL criar DECIMAL(12,2) em vez de um double impreciso.

    /**
     * O JPA exige um construtor sem argumentos para conseguir instanciar
     * o objeto ao ler do banco. Nao remova.
     */
    protected EntidadeEmpregadora() {
    }

    // TODO-2: crie um construtor publico EntidadeEmpregadora(String nome, BigDecimal rendimento)
    //         para o resto do codigo conseguir criar o objeto de forma valida.

    // TODO-3: crie os getters (getNome, getRendimento).
    //         Setters sao opcionais aqui: se a empregadora for imutavel, o cliente
    //         simplesmente troca a lista inteira ao atualizar o cadastro (HU04).
}

package com.xulambs_aluguel.xulu.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente do sistema (HU01 - cadastro, HU03 - login, HU04 - manutencao dos
 * dados). Herda id/login/senha de {@link Usuario}.
 */
@Entity
@DiscriminatorValue("CLIENTE")
public class Cliente extends Usuario {

    public static final int MAX_ENTIDADES_EMPREGADORAS = 3;

    // Sem "nullable = false": a tabela e compartilhada com Agente/Empresa/Banco
    // (SINGLE_TABLE), entao colunas especificas de Cliente ficam NULL nas
    // linhas dos outros tipos. Obrigatoriedade e checada na validacao
    // (ClienteRequest/ClienteService), nao no schema.
    @Column(length = 20)
    private String rg;

    @Column(unique = true, length = 11)
    private String cpf;

    @Column(length = 120)
    private String nome;

    @Column(length = 200)
    private String endereco;

    @Column(length = 100)
    private String profissao;

    /**
     * Ate 3 entidades empregadoras (HU01).
     *
     * @ElementCollection cria uma tabela separada "cliente_empregadora" cujas
     * linhas pertencem a este cliente: apagou o cliente, apagaram as linhas.
     * E exatamente a semantica de composicao do diagrama.
     *
     * A lista ja nasce vazia para nunca ser null -- assim rendimentoTotal()
     * e adicionarEmpregadora() nao precisam checar null.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "cliente_empregadora",
            joinColumns = @JoinColumn(name = "cliente_id")
    )
    private List<EntidadeEmpregadora> entidadesEmpregadoras = new ArrayList<>();

    protected Cliente() {
    }

    public Cliente(String rg, String cpf, String nome, String endereco, String profissao,
                   String login, String senha) {
        super(login, senha);
        this.rg = rg;
        this.cpf = cpf;
        this.nome = nome;
        this.endereco = endereco;
        this.profissao = profissao;
    }

    /**
     * Soma o rendimento de todas as entidades empregadoras do cliente.
     * E o numero que o agente usa para analisar o pedido (HU11).
     */
    public BigDecimal rendimentoTotal() {
        return entidadesEmpregadoras.stream()
                .map(EntidadeEmpregadora::getRendimento)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void adicionarEmpregadora(EntidadeEmpregadora e) {
        if (entidadesEmpregadoras.size() >= MAX_ENTIDADES_EMPREGADORAS) {
            throw new IllegalStateException("Nao e possivel adicionar mais de " + MAX_ENTIDADES_EMPREGADORAS + " empregadoras.");
        }
        entidadesEmpregadoras.add(e);
    }

    /**
     * HU04: altera endereco, profissao e empregadoras. RG e CPF sao imutaveis
     * -- por isso nao existe setRg() nem setCpf().
     */
    public void atualizarDados(String endereco, String profissao, List<EntidadeEmpregadora> empregadoras) {
        this.endereco = endereco;
        this.profissao = profissao;
        this.entidadesEmpregadoras.clear();
        if (empregadoras != null) {
            empregadoras.forEach(this::adicionarEmpregadora);
        }
    }

    public static int getMaxEntidadesEmpregadoras() {
        return MAX_ENTIDADES_EMPREGADORAS;
    }

    public String getRg() {
        return rg;
    }

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getProfissao() {
        return profissao;
    }

    public List<EntidadeEmpregadora> getEntidadesEmpregadoras() {
        return entidadesEmpregadoras;
    }
}

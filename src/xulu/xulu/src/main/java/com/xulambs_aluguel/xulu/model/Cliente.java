package com.xulambs_aluguel.xulu.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente do sistema (HU01 - cadastro, HU04 - manutencao dos dados).
 *
 * Escopo da Sprint 2: apenas o CRUD de cliente. Login/senha e autenticacao
 * (HU03) ficam de fora -- a HU01 exige somente RG, CPF, nome, endereco e
 * profissao.
 *
 * Nesta sprint Cliente e uma entidade independente. Quando Agente/Empresa/Banco
 * entrarem no sistema, extraimos os campos comuns para uma superclasse abstrata
 * Usuario com @Inheritance, como esta no diagrama de classes.
 */
@Entity
@Table(name = "cliente")
public class Cliente {

    public static final int MAX_ENTIDADES_EMPREGADORAS = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String rg;
    private String cpf;
    private String nome;
    private String endereco;
    private String profissao;
    // TODO-4: declare os dados de identificacao exigidos pela HU01:
    //   - rg (String)
    //   - cpf (String)       -> unico! a HU01 diz "recusado se o CPF ja estiver registrado"
    //   - nome (String)
    //   - endereco (String)
    //   - profissao (String)
    // Todos obrigatorios: @Column(nullable = false, length = ...)
    // Pense no length de cada um (cpf tem 11 digitos, endereco precisa de bem mais).

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

    public Cliente(String rg, String cpf, String nome, String endereco, String profissao) {
        this.rg = rg;
        this.cpf = cpf;
        this.nome = nome;
        this.endereco = endereco;
        this.profissao = profissao;
    }

    // TODO-5: construtor publico recebendo os campos obrigatorios do cadastro.
    //         Deixe a lista de empregadoras de fora do construtor -- ela entra
    //         pelo metodo do TODO-7, que e onde a regra do limite de 3 vive.

    /**
     * Soma o rendimento de todas as entidades empregadoras do cliente.
     * E o numero que o agente usa para analisar o pedido (HU11).
     */
    public BigDecimal rendimentoTotal() {
        
           return entidadesEmpregadoras.stream()
                   .map(EntidadeEmpregadora::getRendimento)
                   .reduce(BigDecimal.ZERO, BigDecimal::add);
        return BigDecimal.ZERO;
    }
    
    
    // TODO-7: metodo adicionarEmpregadora(EntidadeEmpregadora e)
    public void adicionarEmpregadora(EntidadeEmpregadora e) {
        if (entidadesEmpregadoras.size() >= MAX_ENTIDADES_EMPREGADORAS) {
            throw new IllegalStateException("Nao e possivel adicionar mais de " + MAX_ENTIDADES_EMPREGADORAS + " empregadoras.");
        }
        entidadesEmpregadoras.add(e);
    }
  


    // TODO-8: metodo atualizarDados(String endereco, String profissao)
    //         A HU04 permite alterar endereco, profissao e empregadoras,
    //         mas RG e CPF sao imutaveis. Nao criar setRg() nem setCpf()
    //         ja e metade da regra: o codigo fica impossivel de usar errado.



    public Long getId() {
        return id;
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

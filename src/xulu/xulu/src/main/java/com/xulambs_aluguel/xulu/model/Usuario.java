package com.xulambs_aluguel.xulu.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

/**
 * Superclasse de todo usuario do sistema (HU03): quem tem login/senha para
 * autenticar. Cliente, Empresa e Banco sao os tipos concretos hoje -- ver
 * docs/diagramas/diagrama-de-classes.puml.
 *
 * Uma unica tabela "usuario" guarda todos os tipos (SINGLE_TABLE), discriminada
 * pela coluna "tipo_usuario". E a opcao mais simples para o escopo atual: poucos
 * campos especificos por subtipo, sem necessidade de joins entre tabelas.
 */
@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario")
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String login;

    /**
     * Sempre armazenada com hash (BCrypt) -- nunca em texto puro. Quem grava
     * aqui e o UsuarioService, que recebe a senha em texto do cadastro e chama
     * {@link #criptografarSenha(String)} antes do save().
     */
    @Column(nullable = false, length = 100)
    private String senha;

    protected Usuario() {
    }

    protected Usuario(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }

    /**
     * Login e imutavel -- por isso nao existe setLogin().
     */
    public void criptografarSenha(String senhaCriptografada) {
        this.senha = senhaCriptografada;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }
}

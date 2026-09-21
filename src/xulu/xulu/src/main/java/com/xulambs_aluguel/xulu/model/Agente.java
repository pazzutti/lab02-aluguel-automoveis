package com.xulambs_aluguel.xulu.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/**
 * Agente que avalia pedidos de aluguel (HU11+): empresas e bancos aderentes.
 * Pedido ainda nao existe no sistema, entao por enquanto Agente so guarda os
 * dados de cadastro/login -- analisarPedido()/registrarParecer() (ver diagrama
 * de classes) entram quando Pedido for implementado.
 */
@Entity
public abstract class Agente extends Usuario {

    // Sem "nullable = false": tabela compartilhada com Cliente (SINGLE_TABLE),
    // entao esta coluna fica NULL nas linhas de Cliente. Obrigatoriedade e
    // checada na validacao (EmpresaRequest/BancoRequest), nao no schema.
    @Column(length = 150)
    private String nomeInstituicao;

    protected Agente() {
    }

    protected Agente(String nomeInstituicao, String login, String senha) {
        super(login, senha);
        this.nomeInstituicao = nomeInstituicao;
    }

    public String getNomeInstituicao() {
        return nomeInstituicao;
    }
}

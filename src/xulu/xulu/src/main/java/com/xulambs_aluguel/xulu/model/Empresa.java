package com.xulambs_aluguel.xulu.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Empresa aderente (HU01/HU03). cadastrarAutomovel()/registrarDevolucao()
 * (ver diagrama de classes) entram quando Automovel/Contrato existirem.
 */
@Entity
@DiscriminatorValue("EMPRESA")
public class Empresa extends Agente {

    protected Empresa() {
    }

    public Empresa(String nomeInstituicao, String login, String senha) {
        super(nomeInstituicao, login, senha);
    }
}

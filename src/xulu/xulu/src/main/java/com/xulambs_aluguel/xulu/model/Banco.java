package com.xulambs_aluguel.xulu.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Banco aderente (HU01/HU03). concederCredito() (ver diagrama de classes)
 * entra quando Leasing/ContratoCredito existirem.
 */
@Entity
@DiscriminatorValue("BANCO")
public class Banco extends Agente {

    protected Banco() {
    }

    public Banco(String nomeInstituicao, String login, String senha) {
        super(nomeInstituicao, login, senha);
    }
}

package com.xulambs_aluguel.xulu.web;

import com.xulambs_aluguel.xulu.model.Agente;

/**
 * Representacao JSON de um agente (empresa ou banco) devolvida pela API.
 */
public record AgenteResponse(Long id, String tipo, String nomeInstituicao, String login) {

    public static AgenteResponse from(Agente agente, String tipo) {
        return new AgenteResponse(agente.getId(), tipo, agente.getNomeInstituicao(), agente.getLogin());
    }
}

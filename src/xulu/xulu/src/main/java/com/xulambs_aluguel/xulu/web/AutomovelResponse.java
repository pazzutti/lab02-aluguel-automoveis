package com.xulambs_aluguel.xulu.web;

import com.xulambs_aluguel.xulu.model.Automovel;

/**
 * Representacao JSON de um automovel devolvida pela API para o frontend Vue.
 */
public record AutomovelResponse(Long id, String placa, int ano, String marca, String modelo) {

    public static AutomovelResponse from(Automovel automovel) {
        return new AutomovelResponse(automovel.getId(), automovel.getPlaca(), automovel.getAno(),
                automovel.getMarca(), automovel.getModelo());
    }
}

package com.xulambs_aluguel.xulu.web;

import com.xulambs_aluguel.xulu.model.Parecer;
import com.xulambs_aluguel.xulu.model.ResultadoParecer;

import java.time.LocalDateTime;

public record ParecerResponse(ResultadoParecer resultado, String justificativa, LocalDateTime dataHora) {

    public static ParecerResponse from(Parecer parecer) {
        return new ParecerResponse(parecer.getResultado(), parecer.getJustificativa(), parecer.getDataHora());
    }
}

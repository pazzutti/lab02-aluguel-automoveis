package com.xulambs_aluguel.xulu.web;

import com.xulambs_aluguel.xulu.model.ContratoCredito;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContratoCreditoResponse(BigDecimal valor, LocalDate dataConcessao, String banco) {

    public static ContratoCreditoResponse from(ContratoCredito credito) {
        return new ContratoCreditoResponse(credito.getValor(), credito.getDataConcessao(),
                credito.getBanco().getNomeInstituicao());
    }
}

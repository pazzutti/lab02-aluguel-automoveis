package com.xulambs_aluguel.xulu.web;

import com.xulambs_aluguel.xulu.model.Assinatura;
import com.xulambs_aluguel.xulu.model.Contrato;
import com.xulambs_aluguel.xulu.model.Leasing;
import com.xulambs_aluguel.xulu.model.Locacao;
import com.xulambs_aluguel.xulu.model.SituacaoContrato;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Representacao JSON de um contrato (Locacao/Assinatura/Leasing) devolvida
 * pela API. Os campos especificos de cada modalidade ficam null quando nao
 * se aplicam -- espelha o SINGLE_TABLE do backend.
 */
public record ContratoResponse(
        Long id,
        String tipo,
        AutomovelResponse automovel,
        String nomeCliente,
        LocalDateTime dataInicio,
        SituacaoContrato situacao,
        Integer prazo,
        LocalDate dataPrevistaDevolucao,
        LocalDate dataEfetivaDevolucao,
        BigDecimal valorMensalidade,
        String recorrencia,
        ContratoCreditoResponse contratoCredito
) {

    public static ContratoResponse from(Contrato contrato) {
        AutomovelResponse automovel = AutomovelResponse.from(contrato.getAutomovel());
        String nomeCliente = contrato.getCliente().getNome();

        if (contrato instanceof Locacao locacao) {
            return new ContratoResponse(locacao.getId(), "LOCACAO", automovel, nomeCliente, locacao.getDataInicio(),
                    locacao.getSituacao(), locacao.getPrazo(), locacao.getDataPrevistaDevolucao(),
                    locacao.getDataEfetivaDevolucao(), null, null, null);
        }
        if (contrato instanceof Assinatura assinatura) {
            return new ContratoResponse(assinatura.getId(), "ASSINATURA", automovel, nomeCliente,
                    assinatura.getDataInicio(), assinatura.getSituacao(), null, null, null,
                    assinatura.getValorMensalidade(), assinatura.getRecorrencia(), null);
        }
        if (contrato instanceof Leasing leasing) {
            ContratoCreditoResponse credito = leasing.getContratoCredito() == null ? null
                    : ContratoCreditoResponse.from(leasing.getContratoCredito());
            return new ContratoResponse(leasing.getId(), "LEASING", automovel, nomeCliente, leasing.getDataInicio(),
                    leasing.getSituacao(), null, null, null, null, null, credito);
        }
        throw new IllegalStateException("Tipo de contrato desconhecido: " + contrato.getClass());
    }
}

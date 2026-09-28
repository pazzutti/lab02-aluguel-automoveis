package com.xulambs_aluguel.xulu.web;

import com.xulambs_aluguel.xulu.model.ModalidadeContrato;
import com.xulambs_aluguel.xulu.model.Pedido;
import com.xulambs_aluguel.xulu.model.SituacaoPedido;

import java.time.LocalDateTime;

/**
 * Representacao JSON de um pedido devolvida pela API. nomeCliente e usado
 * pela tela de pedidos pendentes (visao do agente); na visao do proprio
 * cliente e redundante mas inofensivo.
 */
public record PedidoResponse(
        Long id,
        AutomovelResponse automovel,
        ModalidadeContrato modalidade,
        SituacaoPedido situacao,
        LocalDateTime dataCriacao,
        LocalDateTime dataAlteracao,
        String nomeCliente
) {

    public static PedidoResponse from(Pedido pedido) {
        return new PedidoResponse(pedido.getId(), AutomovelResponse.from(pedido.getAutomovel()),
                pedido.getModalidade(), pedido.getSituacao(), pedido.getDataCriacao(), pedido.getDataAlteracao(),
                pedido.getCliente().getNome());
    }
}

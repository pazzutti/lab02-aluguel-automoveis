package com.xulambs_aluguel.xulu.service;

import com.xulambs_aluguel.xulu.model.Automovel;
import com.xulambs_aluguel.xulu.model.Cliente;
import com.xulambs_aluguel.xulu.model.ModalidadeContrato;
import com.xulambs_aluguel.xulu.model.Pedido;
import com.xulambs_aluguel.xulu.model.SituacaoPedido;
import com.xulambs_aluguel.xulu.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * HU07/HU10 (RF24): cliente cria e consulta os proprios pedidos; agente
 * (empresa/banco) consulta a fila de pendentes. Avaliar o pedido (HU11+)
 * ainda nao tem endpoint proprio.
 */
@Service
public class PedidoService {

    private static final List<SituacaoPedido> SITUACOES_PENDENTES =
            List.of(SituacaoPedido.NAO_AVALIADO, SituacaoPedido.EM_ANALISE);

    private final PedidoRepository pedidoRepository;
    private final AutomovelService automovelService;

    public PedidoService(PedidoRepository pedidoRepository, AutomovelService automovelService) {
        this.pedidoRepository = pedidoRepository;
        this.automovelService = automovelService;
    }

    public Pedido criar(Cliente cliente, Long automovelId, ModalidadeContrato modalidade) {
        Automovel automovel = automovelService.buscarDisponivelPorId(automovelId);
        return pedidoRepository.save(new Pedido(cliente, automovel, modalidade));
    }

    public List<Pedido> listarPorCliente(Cliente cliente) {
        return pedidoRepository.findByClienteOrderByDataCriacaoDesc(cliente);
    }

    public List<Pedido> listarPendentes() {
        return pedidoRepository.findBySituacaoInOrderByDataCriacaoAsc(SITUACOES_PENDENTES);
    }
}

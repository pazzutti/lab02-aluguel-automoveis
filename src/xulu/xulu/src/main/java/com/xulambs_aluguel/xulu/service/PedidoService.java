package com.xulambs_aluguel.xulu.service;

import com.xulambs_aluguel.xulu.model.Agente;
import com.xulambs_aluguel.xulu.model.Automovel;
import com.xulambs_aluguel.xulu.model.Cliente;
import com.xulambs_aluguel.xulu.model.ModalidadeContrato;
import com.xulambs_aluguel.xulu.model.Parecer;
import com.xulambs_aluguel.xulu.model.Pedido;
import com.xulambs_aluguel.xulu.model.ResultadoParecer;
import com.xulambs_aluguel.xulu.model.SituacaoPedido;
import com.xulambs_aluguel.xulu.repository.ParecerRepository;
import com.xulambs_aluguel.xulu.repository.PedidoRepository;
import com.xulambs_aluguel.xulu.web.DecisaoRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * HU07/HU10/HU11 (RF24): cliente cria, consulta, altera e cancela os
 * proprios pedidos; agente (empresa/banco) analisa a fila de pendentes e
 * registra o parecer; cliente decide se aceita o parecer favoravel.
 */
@Service
public class PedidoService {

    private static final List<SituacaoPedido> SITUACOES_PENDENTES =
            List.of(SituacaoPedido.NAO_AVALIADO, SituacaoPedido.EM_ANALISE);

    private final PedidoRepository pedidoRepository;
    private final ParecerRepository parecerRepository;
    private final AutomovelService automovelService;
    private final ContratoService contratoService;

    public PedidoService(PedidoRepository pedidoRepository, ParecerRepository parecerRepository,
                          AutomovelService automovelService, ContratoService contratoService) {
        this.pedidoRepository = pedidoRepository;
        this.parecerRepository = parecerRepository;
        this.automovelService = automovelService;
        this.contratoService = contratoService;
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

    public Pedido iniciarAnalise(Long pedidoId) {
        Pedido pedido = buscarPorId(pedidoId);
        pedido.iniciarAnalise();
        return pedidoRepository.save(pedido);
    }

    public Pedido registrarParecer(Long pedidoId, Agente agente, ResultadoParecer resultado, String justificativa) {
        Pedido pedido = buscarPorId(pedidoId);
        pedido.avaliar(resultado);
        Parecer parecer = new Parecer(pedido, agente, resultado, justificativa);
        parecerRepository.save(parecer);
        return pedidoRepository.save(pedido);
    }

    public Pedido decidir(Long pedidoId, Cliente cliente, DecisaoRequest request) {
        Pedido pedido = buscarDoCliente(pedidoId, cliente);
        pedido.decidir(request.aceitar());
        pedidoRepository.save(pedido);
        if (request.aceitar()) {
            contratoService.criar(pedido, request);
        }
        return pedido;
    }

    public Pedido cancelar(Long pedidoId, Cliente cliente) {
        Pedido pedido = buscarDoCliente(pedidoId, cliente);
        pedido.cancelar();
        return pedidoRepository.save(pedido);
    }

    public Pedido alterar(Long pedidoId, Cliente cliente, Long automovelId, ModalidadeContrato modalidade) {
        Pedido pedido = buscarDoCliente(pedidoId, cliente);
        Automovel automovel = automovelService.buscarDisponivelPorId(automovelId);
        pedido.alterar(automovel, modalidade);
        return pedidoRepository.save(pedido);
    }

    private Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pedido nao encontrado: " + id));
    }

    /**
     * Igual ao restante do sistema (ver ClienteRestController): um pedido de
     * outro cliente "nao existe" para quem esta pedindo, nao e so proibido.
     */
    private Pedido buscarDoCliente(Long id, Cliente cliente) {
        Pedido pedido = buscarPorId(id);
        if (!pedido.getCliente().getId().equals(cliente.getId())) {
            throw new NoSuchElementException("Pedido nao encontrado: " + id);
        }
        return pedido;
    }
}

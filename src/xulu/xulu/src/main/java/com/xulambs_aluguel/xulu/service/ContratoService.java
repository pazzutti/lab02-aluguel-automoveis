package com.xulambs_aluguel.xulu.service;

import com.xulambs_aluguel.xulu.model.Assinatura;
import com.xulambs_aluguel.xulu.model.Banco;
import com.xulambs_aluguel.xulu.model.Cliente;
import com.xulambs_aluguel.xulu.model.Contrato;
import com.xulambs_aluguel.xulu.model.ContratoCredito;
import com.xulambs_aluguel.xulu.model.Leasing;
import com.xulambs_aluguel.xulu.model.Locacao;
import com.xulambs_aluguel.xulu.model.ModalidadeContrato;
import com.xulambs_aluguel.xulu.model.Pedido;
import com.xulambs_aluguel.xulu.model.SituacaoContrato;
import com.xulambs_aluguel.xulu.repository.ContratoCreditoRepository;
import com.xulambs_aluguel.xulu.repository.ContratoRepository;
import com.xulambs_aluguel.xulu.web.DecisaoRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Criacao e ciclo de vida dos contratos originados de um Pedido aceito
 * (ver diagrama de classes: Locacao/Assinatura/Leasing).
 */
@Service
public class ContratoService {

    private final ContratoRepository contratoRepository;
    private final ContratoCreditoRepository contratoCreditoRepository;
    private final AutomovelService automovelService;

    public ContratoService(ContratoRepository contratoRepository, ContratoCreditoRepository contratoCreditoRepository,
                            AutomovelService automovelService) {
        this.contratoRepository = contratoRepository;
        this.contratoCreditoRepository = contratoCreditoRepository;
        this.automovelService = automovelService;
    }

    public Contrato criar(Pedido pedido, DecisaoRequest request) {
        Contrato contrato = construir(pedido, request);
        contratoRepository.save(contrato);
        automovelService.marcarIndisponivel(pedido.getAutomovel());
        return contrato;
    }

    private Contrato construir(Pedido pedido, DecisaoRequest request) {
        ModalidadeContrato modalidade = pedido.getModalidade();
        if (modalidade == ModalidadeContrato.LOCACAO) {
            if (request.prazo() == null || request.dataPrevistaDevolucao() == null) {
                throw new IllegalArgumentException("Locacao exige prazo e data prevista de devolucao");
            }
            return new Locacao(pedido, request.prazo(), request.dataPrevistaDevolucao());
        }
        if (modalidade == ModalidadeContrato.ASSINATURA) {
            if (request.valorMensalidade() == null || request.recorrencia() == null || request.recorrencia().isBlank()) {
                throw new IllegalArgumentException("Assinatura exige valor da mensalidade e recorrencia");
            }
            return new Assinatura(pedido, request.valorMensalidade(), request.recorrencia());
        }
        return new Leasing(pedido);
    }

    public List<Contrato> listarPorCliente(Cliente cliente) {
        return contratoRepository.findByClienteOrderByDataInicioDesc(cliente);
    }

    public List<Locacao> listarLocacoesAtivas() {
        return contratoRepository.buscarLocacoesPorSituacao(SituacaoContrato.EM_EXECUCAO);
    }

    public List<Leasing> listarLeasingsPendentes() {
        return contratoRepository.buscarLeasingsPorSituacao(SituacaoContrato.PENDENTE);
    }

    /**
     * HU da Empresa (Empresa.registrarDevolucao no diagrama).
     */
    public Locacao registrarDevolucao(Long contratoId, java.time.LocalDate data) {
        Locacao locacao = buscarLocacao(contratoId);
        locacao.registrarDevolucao(data);
        contratoRepository.save(locacao);
        automovelService.marcarDisponivel(locacao.getAutomovel());
        return locacao;
    }

    /**
     * HU do Banco (Banco.concederCredito no diagrama).
     */
    public Leasing concederCredito(Long contratoId, Banco banco, java.math.BigDecimal valor) {
        Leasing leasing = buscarLeasing(contratoId);
        ContratoCredito credito = contratoCreditoRepository.save(new ContratoCredito(valor, banco));
        leasing.concederCredito(credito);
        contratoRepository.save(leasing);
        return leasing;
    }

    private Locacao buscarLocacao(Long id) {
        Contrato contrato = contratoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Contrato nao encontrado: " + id));
        if (!(contrato instanceof Locacao locacao)) {
            throw new IllegalArgumentException("Contrato " + id + " nao e uma locacao");
        }
        return locacao;
    }

    private Leasing buscarLeasing(Long id) {
        Contrato contrato = contratoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Contrato nao encontrado: " + id));
        if (!(contrato instanceof Leasing leasing)) {
            throw new IllegalArgumentException("Contrato " + id + " nao e um leasing");
        }
        return leasing;
    }
}

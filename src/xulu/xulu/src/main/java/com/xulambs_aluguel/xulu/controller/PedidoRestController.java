package com.xulambs_aluguel.xulu.controller;

import com.xulambs_aluguel.xulu.model.Agente;
import com.xulambs_aluguel.xulu.model.Cliente;
import com.xulambs_aluguel.xulu.service.AgenteService;
import com.xulambs_aluguel.xulu.service.ClienteService;
import com.xulambs_aluguel.xulu.service.PedidoService;
import com.xulambs_aluguel.xulu.web.DecisaoRequest;
import com.xulambs_aluguel.xulu.web.ParecerRequest;
import com.xulambs_aluguel.xulu.web.PedidoRequest;
import com.xulambs_aluguel.xulu.web.PedidoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * API REST de pedidos de aluguel (HU07/HU10/HU11, RF24): cliente cria,
 * consulta, altera e cancela os proprios pedidos; agente (empresa/banco)
 * analisa a fila de pendentes e registra o parecer; cliente decide se aceita
 * o parecer favoravel (o que gera o contrato).
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoRestController {

    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private final AgenteService agenteService;

    public PedidoRestController(PedidoService pedidoService, ClienteService clienteService,
                                 AgenteService agenteService) {
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
        this.agenteService = agenteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CLIENTE')")
    public PedidoResponse criar(Authentication authentication, @Valid @RequestBody PedidoRequest request) {
        Cliente cliente = clienteService.buscarPorLogin(authentication.getName());
        return PedidoResponse.from(pedidoService.criar(cliente, request.automovelId(), request.modalidade()));
    }

    @GetMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public List<PedidoResponse> meusPedidos(Authentication authentication) {
        Cliente cliente = clienteService.buscarPorLogin(authentication.getName());
        return pedidoService.listarPorCliente(cliente).stream()
                .map(PedidoResponse::from)
                .toList();
    }

    @GetMapping("/pendentes")
    @PreAuthorize("hasAnyRole('EMPRESA', 'BANCO')")
    public List<PedidoResponse> pendentes() {
        return pedidoService.listarPendentes().stream()
                .map(PedidoResponse::from)
                .toList();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public PedidoResponse alterar(Authentication authentication, @PathVariable Long id,
                                   @Valid @RequestBody PedidoRequest request) {
        Cliente cliente = clienteService.buscarPorLogin(authentication.getName());
        return PedidoResponse.from(
                pedidoService.alterar(id, cliente, request.automovelId(), request.modalidade()));
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('CLIENTE')")
    public PedidoResponse cancelar(Authentication authentication, @PathVariable Long id) {
        Cliente cliente = clienteService.buscarPorLogin(authentication.getName());
        return PedidoResponse.from(pedidoService.cancelar(id, cliente));
    }

    @PostMapping("/{id}/analise")
    @PreAuthorize("hasAnyRole('EMPRESA', 'BANCO')")
    public PedidoResponse iniciarAnalise(@PathVariable Long id) {
        return PedidoResponse.from(pedidoService.iniciarAnalise(id));
    }

    @PostMapping("/{id}/parecer")
    @PreAuthorize("hasAnyRole('EMPRESA', 'BANCO')")
    public PedidoResponse registrarParecer(Authentication authentication, @PathVariable Long id,
                                            @Valid @RequestBody ParecerRequest request) {
        Agente agente = agenteService.buscarPorLogin(authentication.getName());
        return PedidoResponse.from(
                pedidoService.registrarParecer(id, agente, request.resultado(), request.justificativa()));
    }

    @PostMapping("/{id}/decisao")
    @PreAuthorize("hasRole('CLIENTE')")
    public PedidoResponse decidir(Authentication authentication, @PathVariable Long id,
                                   @Valid @RequestBody DecisaoRequest request) {
        Cliente cliente = clienteService.buscarPorLogin(authentication.getName());
        return PedidoResponse.from(pedidoService.decidir(id, cliente, request));
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> tratarNaoEncontrado(NoSuchElementException e) {
        return Map.of("mensagem", e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> tratarRequisicaoInvalida(RuntimeException e) {
        return Map.of("mensagem", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> tratarValidacao(MethodArgumentNotValidException e) {
        String mensagem = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Dados invalidos");
        return Map.of("mensagem", mensagem);
    }
}

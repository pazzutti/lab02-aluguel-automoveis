package com.xulambs_aluguel.xulu.controller;

import com.xulambs_aluguel.xulu.model.Cliente;
import com.xulambs_aluguel.xulu.service.ClienteService;
import com.xulambs_aluguel.xulu.service.PedidoService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * API REST de pedidos de aluguel (HU07/HU10, RF24): cliente cria e consulta
 * os proprios pedidos; agente (empresa/banco) consulta a fila de pendentes.
 * Avaliar o pedido (HU11+) ainda nao tem endpoint proprio.
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoRestController {

    private final PedidoService pedidoService;
    private final ClienteService clienteService;

    public PedidoRestController(PedidoService pedidoService, ClienteService clienteService) {
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
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

package com.xulambs_aluguel.xulu.controller;

import com.xulambs_aluguel.xulu.model.Banco;
import com.xulambs_aluguel.xulu.model.Cliente;
import com.xulambs_aluguel.xulu.service.AgenteService;
import com.xulambs_aluguel.xulu.service.ClienteService;
import com.xulambs_aluguel.xulu.service.ContratoService;
import com.xulambs_aluguel.xulu.web.ContratoResponse;
import com.xulambs_aluguel.xulu.web.CreditoRequest;
import com.xulambs_aluguel.xulu.web.DevolucaoRequest;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * API REST de contratos (Locacao/Assinatura/Leasing): cliente consulta os
 * proprios; empresa acompanha locacoes ativas e registra devolucao; banco
 * acompanha leasings pendentes e concede credito (ver diagrama de classes).
 */
@RestController
@RequestMapping("/api/contratos")
public class ContratoRestController {

    private final ContratoService contratoService;
    private final ClienteService clienteService;
    private final AgenteService agenteService;

    public ContratoRestController(ContratoService contratoService, ClienteService clienteService,
                                   AgenteService agenteService) {
        this.contratoService = contratoService;
        this.clienteService = clienteService;
        this.agenteService = agenteService;
    }

    @GetMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public List<ContratoResponse> meusContratos(Authentication authentication) {
        Cliente cliente = clienteService.buscarPorLogin(authentication.getName());
        return contratoService.listarPorCliente(cliente).stream()
                .map(ContratoResponse::from)
                .toList();
    }

    @GetMapping("/locacoes-ativas")
    @PreAuthorize("hasRole('EMPRESA')")
    public List<ContratoResponse> locacoesAtivas() {
        return contratoService.listarLocacoesAtivas().stream()
                .map(ContratoResponse::from)
                .toList();
    }

    @PostMapping("/{id}/devolucao")
    @PreAuthorize("hasRole('EMPRESA')")
    public ContratoResponse registrarDevolucao(@PathVariable Long id, @Valid @RequestBody DevolucaoRequest request) {
        return ContratoResponse.from(contratoService.registrarDevolucao(id, request.data()));
    }

    @GetMapping("/leasings-pendentes")
    @PreAuthorize("hasRole('BANCO')")
    public List<ContratoResponse> leasingsPendentes() {
        return contratoService.listarLeasingsPendentes().stream()
                .map(ContratoResponse::from)
                .toList();
    }

    @PostMapping("/{id}/credito")
    @PreAuthorize("hasRole('BANCO')")
    public ContratoResponse concederCredito(Authentication authentication, @PathVariable Long id,
                                             @Valid @RequestBody CreditoRequest request) {
        Banco banco = agenteService.buscarBancoPorLogin(authentication.getName());
        return ContratoResponse.from(contratoService.concederCredito(id, banco, request.valor()));
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

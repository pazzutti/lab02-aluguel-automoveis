package com.xulambs_aluguel.xulu.controller;

import com.xulambs_aluguel.xulu.model.Empresa;
import com.xulambs_aluguel.xulu.service.AgenteService;
import com.xulambs_aluguel.xulu.service.AutomovelService;
import com.xulambs_aluguel.xulu.web.AutomovelRequest;
import com.xulambs_aluguel.xulu.web.AutomovelResponse;
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
 * Consulta de automoveis disponiveis (HU07) e cadastro de frota pela Empresa
 * proprietaria (HU: Empresa.cadastrarAutomovel no diagrama).
 */
@RestController
@RequestMapping("/api/automoveis")
public class AutomovelRestController {

    private final AutomovelService automovelService;
    private final AgenteService agenteService;

    public AutomovelRestController(AutomovelService automovelService, AgenteService agenteService) {
        this.automovelService = automovelService;
        this.agenteService = agenteService;
    }

    @GetMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public List<AutomovelResponse> listarDisponiveis() {
        return automovelService.listarDisponiveis().stream()
                .map(AutomovelResponse::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('EMPRESA')")
    public AutomovelResponse cadastrar(Authentication authentication, @Valid @RequestBody AutomovelRequest request) {
        Empresa empresa = agenteService.buscarEmpresaPorLogin(authentication.getName());
        return AutomovelResponse.from(
                automovelService.cadastrar(empresa, request.placa(), request.ano(), request.marca(), request.modelo()));
    }

    @GetMapping("/meus")
    @PreAuthorize("hasRole('EMPRESA')")
    public List<AutomovelResponse> meusAutomoveis(Authentication authentication) {
        Empresa empresa = agenteService.buscarEmpresaPorLogin(authentication.getName());
        return automovelService.listarPorProprietario(empresa).stream()
                .map(AutomovelResponse::from)
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

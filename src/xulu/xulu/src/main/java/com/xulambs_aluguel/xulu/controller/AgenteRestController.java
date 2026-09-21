package com.xulambs_aluguel.xulu.controller;

import com.xulambs_aluguel.xulu.model.Banco;
import com.xulambs_aluguel.xulu.model.Empresa;
import com.xulambs_aluguel.xulu.service.AgenteService;
import com.xulambs_aluguel.xulu.web.AgenteResponse;
import com.xulambs_aluguel.xulu.web.BancoRequest;
import com.xulambs_aluguel.xulu.web.EmpresaRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Cadastro dos agentes aderentes (HU01/HU03): empresas e bancos. API REST
 * consumida pelo frontend Vue (tela de registro com selecao de tipo de conta).
 */
@RestController
@RequestMapping("/api")
public class AgenteRestController {

    private final AgenteService agenteService;

    public AgenteRestController(AgenteService agenteService) {
        this.agenteService = agenteService;
    }

    @PostMapping("/empresas")
    @ResponseStatus(HttpStatus.CREATED)
    public AgenteResponse cadastrarEmpresa(@Valid @RequestBody EmpresaRequest request) {
        Empresa empresa = new Empresa(request.nomeInstituicao(), request.login(), request.senha());
        return AgenteResponse.from(agenteService.cadastrarEmpresa(empresa), "EMPRESA");
    }

    @PostMapping("/bancos")
    @ResponseStatus(HttpStatus.CREATED)
    public AgenteResponse cadastrarBanco(@Valid @RequestBody BancoRequest request) {
        Banco banco = new Banco(request.nomeInstituicao(), request.login(), request.senha());
        return AgenteResponse.from(agenteService.cadastrarBanco(banco), "BANCO");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> tratarRequisicaoInvalida(IllegalArgumentException e) {
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

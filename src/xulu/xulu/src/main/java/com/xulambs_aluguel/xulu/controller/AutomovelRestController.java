package com.xulambs_aluguel.xulu.controller;

import com.xulambs_aluguel.xulu.service.AutomovelService;
import com.xulambs_aluguel.xulu.web.AutomovelResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Consulta de automoveis disponiveis, usada pela tela de novo pedido (HU07).
 * Cadastro de automovel (Empresa.cadastrarAutomovel) ainda nao tem endpoint.
 */
@RestController
@RequestMapping("/api/automoveis")
public class AutomovelRestController {

    private final AutomovelService automovelService;

    public AutomovelRestController(AutomovelService automovelService) {
        this.automovelService = automovelService;
    }

    @GetMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public List<AutomovelResponse> listarDisponiveis() {
        return automovelService.listarDisponiveis().stream()
                .map(AutomovelResponse::from)
                .toList();
    }
}

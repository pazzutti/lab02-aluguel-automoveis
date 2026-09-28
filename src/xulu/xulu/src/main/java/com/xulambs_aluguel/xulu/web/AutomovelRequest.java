package com.xulambs_aluguel.xulu.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Corpo JSON esperado no cadastro de um automovel pela Empresa
 * (POST /api/automoveis).
 */
public record AutomovelRequest(
        @NotBlank(message = "Placa e obrigatoria") String placa,
        @NotNull(message = "Ano e obrigatorio") Integer ano,
        @NotBlank(message = "Marca e obrigatoria") String marca,
        @NotBlank(message = "Modelo e obrigatorio") String modelo
) {
}

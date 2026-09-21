package com.xulambs_aluguel.xulu.web;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * Corpo JSON esperado na atualizacao de um cliente (PUT /api/clientes/{id}).
 * RG e CPF nao entram aqui: sao imutaveis (HU04).
 */
public record ClienteUpdateRequest(
        @NotBlank(message = "Endereco e obrigatorio") String endereco,
        @NotBlank(message = "Profissao e obrigatoria") String profissao,
        List<EmpregadoraDTO> empregadoras
) {
}

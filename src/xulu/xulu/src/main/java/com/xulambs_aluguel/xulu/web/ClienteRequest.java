package com.xulambs_aluguel.xulu.web;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * Corpo JSON esperado no cadastro de um cliente (POST /api/clientes).
 */
public record ClienteRequest(
        @NotBlank(message = "RG e obrigatorio") String rg,
        @NotBlank(message = "CPF e obrigatorio") String cpf,
        @NotBlank(message = "Nome e obrigatorio") String nome,
        @NotBlank(message = "Endereco e obrigatorio") String endereco,
        @NotBlank(message = "Profissao e obrigatoria") String profissao,
        @NotBlank(message = "Login e obrigatorio") String login,
        @NotBlank(message = "Senha e obrigatoria") String senha,
        List<EmpregadoraDTO> empregadoras
) {
}

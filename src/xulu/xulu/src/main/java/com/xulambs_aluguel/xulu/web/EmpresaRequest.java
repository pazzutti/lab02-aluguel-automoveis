package com.xulambs_aluguel.xulu.web;

import jakarta.validation.constraints.NotBlank;

/**
 * Corpo JSON esperado no cadastro de uma empresa (POST /api/empresas).
 */
public record EmpresaRequest(
        @NotBlank(message = "Nome da instituicao e obrigatorio") String nomeInstituicao,
        @NotBlank(message = "Login e obrigatorio") String login,
        @NotBlank(message = "Senha e obrigatoria") String senha
) {
}

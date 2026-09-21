package com.xulambs_aluguel.xulu.web;

import java.math.BigDecimal;

/**
 * Representacao JSON de uma entidade empregadora, usada tanto para receber
 * dados do frontend Vue quanto para devolve-los na resposta da API.
 */
public record EmpregadoraDTO(String nome, BigDecimal rendimento) {
}

package com.xulambs_aluguel.xulu.web;

import com.xulambs_aluguel.xulu.model.Cliente;

import java.math.BigDecimal;
import java.util.List;

/**
 * Representacao JSON de um cliente devolvida pela API para o frontend Vue.
 */
public record ClienteResponse(
        Long id,
        String rg,
        String cpf,
        String nome,
        String endereco,
        String profissao,
        BigDecimal rendimentoTotal,
        List<EmpregadoraDTO> empregadoras
) {

    public static ClienteResponse from(Cliente cliente) {
        List<EmpregadoraDTO> empregadoras = cliente.getEntidadesEmpregadoras().stream()
                .map(e -> new EmpregadoraDTO(e.getNome(), e.getRendimento()))
                .toList();

        return new ClienteResponse(cliente.getId(), cliente.getRg(), cliente.getCpf(), cliente.getNome(),
                cliente.getEndereco(), cliente.getProfissao(), cliente.rendimentoTotal(), empregadoras);
    }
}

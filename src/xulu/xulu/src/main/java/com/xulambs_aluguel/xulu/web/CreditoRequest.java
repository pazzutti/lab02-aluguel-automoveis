package com.xulambs_aluguel.xulu.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreditoRequest(
        @NotNull(message = "Valor e obrigatorio") @Positive(message = "Valor deve ser positivo") BigDecimal valor
) {
}

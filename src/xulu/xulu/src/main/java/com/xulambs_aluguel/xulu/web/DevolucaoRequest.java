package com.xulambs_aluguel.xulu.web;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DevolucaoRequest(@NotNull(message = "Data de devolucao e obrigatoria") LocalDate data) {
}

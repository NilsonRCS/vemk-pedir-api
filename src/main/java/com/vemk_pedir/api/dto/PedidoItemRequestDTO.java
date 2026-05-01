package com.vemk_pedir.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PedidoItemRequestDTO(
    @NotBlank(message = "produto e obrigatorio")
    String produto,
    @NotNull(message = "quantidade e obrigatoria")
    @Min(value = 1, message = "quantidade deve ser maior que zero")
    Integer quantidade
) {
}

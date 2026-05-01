package com.vemk_pedir.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.List;

public record PedidoRequestDTO(
    @NotBlank(message = "textoOriginal e obrigatorio")
    String textoOriginal,
    String cliente,
    LocalDate dataEntrega,
    @NotEmpty(message = "itens e obrigatorio")
    List<@Valid PedidoItemRequestDTO> itens
) {
}

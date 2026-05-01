package com.vemk_pedir.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public record PedidoRequestDTO(
    @NotBlank(message = "textoOriginal e obrigatorio")
    String textoOriginal,
    String cliente,
    LocalDate dataEntrega,
    List<@Valid PedidoItemRequestDTO> itens
) {
}

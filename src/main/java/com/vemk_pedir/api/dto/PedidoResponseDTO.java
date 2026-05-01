package com.vemk_pedir.api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponseDTO(
    Long id,
    String textoOriginal,
    String cliente,
    LocalDate dataEntrega,
    List<PedidoItemResponseDTO> itens,
    LocalDateTime createdAt
) {
}

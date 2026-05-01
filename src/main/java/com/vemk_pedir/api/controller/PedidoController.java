package com.vemk_pedir.api.controller;

import com.vemk_pedir.api.dto.PedidoItemResponseDTO;
import com.vemk_pedir.api.dto.PedidoRequestDTO;
import com.vemk_pedir.api.dto.PedidoResponseDTO;
import com.vemk_pedir.api.model.Pedido;
import com.vemk_pedir.api.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/pedidos")
    public List<PedidoResponseDTO> listar() {
        return pedidoService.listarTodos().stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/pedido/{id}")
    public PedidoResponseDTO buscarPorId(@PathVariable Long id) {
        return toResponse(pedidoService.buscarPorId(id));
    }

    @PostMapping("/pedido")
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponseDTO criar(@Valid @RequestBody PedidoRequestDTO dto) {
        Pedido pedido = pedidoService.criar(dto);
        return toResponse(pedido);
    }

    private PedidoResponseDTO toResponse(Pedido pedido) {
        List<PedidoItemResponseDTO> itens = pedido.getItens().stream()
            .map(item -> new PedidoItemResponseDTO(item.getProduto(), item.getQuantidade()))
            .toList();

        return new PedidoResponseDTO(
            pedido.getId(),
            pedido.getTextoOriginal(),
            pedido.getCliente(),
            pedido.getDataEntrega(),
            itens,
            pedido.getCreatedAt()
        );
    }
}

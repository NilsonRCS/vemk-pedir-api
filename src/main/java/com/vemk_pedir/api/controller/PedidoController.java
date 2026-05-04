package com.vemk_pedir.api.controller;

import com.vemk_pedir.api.dto.PedidoRequestDTO;
import com.vemk_pedir.api.dto.PedidoResponseDTO;
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
        return pedidoService.listarTodos();
    }

    @GetMapping("/pedido/{id}")
    public PedidoResponseDTO buscarPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id);
    }

    @PostMapping("/pedido")
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponseDTO criar(@Valid @RequestBody PedidoRequestDTO dto) {
        return pedidoService.criar(dto);
    }
}

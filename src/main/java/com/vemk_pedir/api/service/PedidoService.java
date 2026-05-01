package com.vemk_pedir.api.service;

import com.vemk_pedir.api.dto.PedidoItemRequestDTO;
import com.vemk_pedir.api.dto.PedidoRequestDTO;
import com.vemk_pedir.api.model.ItemPedido;
import com.vemk_pedir.api.model.Pedido;
import com.vemk_pedir.api.repository.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public Pedido criar(PedidoRequestDTO dto) {
        Pedido pedido = new Pedido();
        pedido.setTextoOriginal(dto.textoOriginal());
        pedido.setCliente(dto.cliente());
        pedido.setDataEntrega(dto.dataEntrega());

        for (PedidoItemRequestDTO itemDto : dto.itens()) {
            ItemPedido item = new ItemPedido();
            item.setProduto(itemDto.produto());
            item.setQuantidade(itemDto.quantidade());
            pedido.addItem(item);
        }

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido nao encontrado"));
    }
}

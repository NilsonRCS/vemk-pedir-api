package com.vemk_pedir.api.service;

import com.vemk_pedir.api.dto.PedidoItemRequestDTO;
import com.vemk_pedir.api.dto.PedidoItemResponseDTO;
import com.vemk_pedir.api.dto.PedidoRequestDTO;
import com.vemk_pedir.api.dto.PedidoResponseDTO;
import com.vemk_pedir.api.model.ItemPedido;
import com.vemk_pedir.api.model.Pedido;
import com.vemk_pedir.api.repository.PedidoRepository;
import com.vemk_pedir.api.service.IAParsingService.ParsedItem;
import com.vemk_pedir.api.service.IAParsingService.ParsedPedido;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ParserFacadeService parserFacadeService;

    public PedidoService(PedidoRepository pedidoRepository, ParserFacadeService parserFacadeService) {
        this.pedidoRepository = pedidoRepository;
        this.parserFacadeService = parserFacadeService;
    }

    public PedidoResponseDTO criar(PedidoRequestDTO dto) {
        Pedido pedido = new Pedido();
        pedido.setTextoOriginal(dto.textoOriginal());
        pedido.setCliente(dto.cliente() != null && !dto.cliente().isBlank() ? dto.cliente() : "desconhecido");

        ParsedPedido parsedPedido = parserFacadeService.parse(dto.textoOriginal());
        pedido.setDataEntrega(dto.dataEntrega() != null ? dto.dataEntrega() : parsedPedido.dataEntrega());

        List<PedidoItemRequestDTO> itensInformados = dto.itens();
        if (itensInformados == null || itensInformados.isEmpty()) {
            for (ParsedItem parsedItem : parsedPedido.itens()) {
                ItemPedido item = new ItemPedido();
                item.setProduto(parsedItem.produto());
                item.setQuantidade(parsedItem.quantidade());
                pedido.addItem(item);
            }
        } else {
            for (PedidoItemRequestDTO itemDto : itensInformados) {
                ItemPedido item = new ItemPedido();
                item.setProduto(itemDto.produto());
                item.setQuantidade(itemDto.quantidade());
                pedido.addItem(item);
            }
        }

        if (pedido.getItens().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nao foi possivel extrair itens do pedido");
        }

        return toResponse(pedidoRepository.save(pedido));
    }

    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido nao encontrado"));
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

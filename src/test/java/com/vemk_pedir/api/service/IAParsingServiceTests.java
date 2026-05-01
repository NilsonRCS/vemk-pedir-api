package com.vemk_pedir.api.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IAParsingServiceTests {

    private final IAParsingService iaParsingService = new IAParsingService();

    @Test
    void deveExtrairItensEDataDaFraseExemplo() {
        String texto = "Quero 10 caixas de leite integral e 5 fardos de agua para entrega amanha";

        IAParsingService.ParsedPedido parsed = iaParsingService.parseTextoLivre(texto);

        assertNotNull(parsed);
        assertEquals(LocalDate.now().plusDays(1), parsed.dataEntrega());

        List<IAParsingService.ParsedItem> itens = parsed.itens();
        assertEquals(2, itens.size());

        assertEquals("leite integral", itens.get(0).produto());
        assertEquals(10, itens.get(0).quantidade());

        assertEquals("agua", itens.get(1).produto());
        assertEquals(5, itens.get(1).quantidade());
    }
}

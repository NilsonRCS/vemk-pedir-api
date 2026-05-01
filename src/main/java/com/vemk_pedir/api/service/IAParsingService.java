package com.vemk_pedir.api.service;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IAParsingService {

    private static final Pattern ITEM_PATTERN = Pattern.compile(
        "(\\d+)\\s+(?:caixas?|fardos?|unidades?|pacotes?|garrafas?|latas?|kg|quilo(?:s)?|litros?)?\\s*(?:de\\s+)?([a-z0-9\\s]+?)(?=\\s+e\\s+\\d+|\\s+para\\b|$)",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern ISO_DATE_PATTERN = Pattern.compile("\\b(\\d{4})-(\\d{2})-(\\d{2})\\b");
    private static final Pattern BR_DATE_PATTERN = Pattern.compile("\\b(\\d{1,2})[/-](\\d{1,2})[/-](\\d{4})\\b");

    public ParsedPedido parseTextoLivre(String textoOriginal) {
        if (textoOriginal == null || textoOriginal.isBlank()) {
            return new ParsedPedido(List.of(), null);
        }

        String textoNormalizado = normalizarTexto(textoOriginal);
        LocalDate dataEntrega = extrairData(textoNormalizado);
        List<ParsedItem> itens = extrairItens(textoNormalizado);

        return new ParsedPedido(itens, dataEntrega);
    }

    private String normalizarTexto(String texto) {
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replaceAll("\\p{M}+", "");
        return semAcento
            .toLowerCase(Locale.ROOT)
            .replaceAll("[,.;:]", " ")
            .replaceAll("\\s+", " ")
            .trim();
    }

    private LocalDate extrairData(String texto) {
        Matcher isoDate = ISO_DATE_PATTERN.matcher(texto);
        if (isoDate.find()) {
            return LocalDate.of(
                Integer.parseInt(isoDate.group(1)),
                Integer.parseInt(isoDate.group(2)),
                Integer.parseInt(isoDate.group(3))
            );
        }

        Matcher brDate = BR_DATE_PATTERN.matcher(texto);
        if (brDate.find()) {
            return LocalDate.of(
                Integer.parseInt(brDate.group(3)),
                Integer.parseInt(brDate.group(2)),
                Integer.parseInt(brDate.group(1))
            );
        }

        if (texto.contains("amanha")) {
            return LocalDate.now().plusDays(1);
        }

        if (texto.contains("hoje")) {
            return LocalDate.now();
        }

        return null;
    }

    private List<ParsedItem> extrairItens(String texto) {
        Matcher matcher = ITEM_PATTERN.matcher(texto);
        List<ParsedItem> itens = new ArrayList<>();

        while (matcher.find()) {
            int quantidade = Integer.parseInt(matcher.group(1));
            String produto = matcher.group(2)
                .replaceAll("\\b(para|entrega|hoje|amanha)\\b.*$", "")
                .replaceAll("\\s+", " ")
                .trim();

            if (!produto.isBlank()) {
                itens.add(new ParsedItem(produto, quantidade));
            }
        }

        return itens;
    }

    public record ParsedPedido(List<ParsedItem> itens, LocalDate dataEntrega) {
    }

    public record ParsedItem(String produto, int quantidade) {
    }
}

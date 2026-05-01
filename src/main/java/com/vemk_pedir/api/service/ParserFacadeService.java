package com.vemk_pedir.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ParserFacadeService {

    private static final Logger log = LoggerFactory.getLogger(ParserFacadeService.class);

    private final GeminiParsingService geminiParsingService;
    private final IAParsingService iaParsingService;

    public ParserFacadeService(GeminiParsingService geminiParsingService, IAParsingService iaParsingService) {
        this.geminiParsingService = geminiParsingService;
        this.iaParsingService = iaParsingService;
    }

    public IAParsingService.ParsedPedido parse(String textoOriginal) {
        Optional<IAParsingService.ParsedPedido> geminiResult = geminiParsingService.parse(textoOriginal);
        if (geminiResult.isPresent()) {
            log.info("Parsing via Gemini LLM");
            return geminiResult.get();
        }
        log.info("Parsing via regex (fallback)");
        return iaParsingService.parseTextoLivre(textoOriginal);
    }
}

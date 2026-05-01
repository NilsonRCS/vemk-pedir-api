package com.vemk_pedir.api.service;

import org.springframework.stereotype.Service;

@Service
public class IAParsingService {

    public String inferirStatusInicial(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            return "NOVO";
        }
        return "PENDENTE_ANALISE";
    }
}

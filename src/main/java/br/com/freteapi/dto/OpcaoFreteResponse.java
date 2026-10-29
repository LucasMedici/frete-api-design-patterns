package br.com.freteapi.dto;

import java.math.BigDecimal;

public record OpcaoFreteResponse(
        String tipoFrete,
        String descricao,
        BigDecimal valor,
        int prazoDias) {
}

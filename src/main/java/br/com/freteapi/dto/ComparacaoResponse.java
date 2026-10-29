package br.com.freteapi.dto;

import java.util.List;

public record ComparacaoResponse(
        String cidade,
        String uf,
        boolean interestadual,
        List<OpcaoFreteResponse> opcoes) {
}

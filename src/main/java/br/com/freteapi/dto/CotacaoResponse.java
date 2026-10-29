package br.com.freteapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.freteapi.model.CotacaoFrete;

public record CotacaoResponse(
        Long id,
        String tipoFrete,
        String cep,
        String cidade,
        String uf,
        double pesoKg,
        BigDecimal valor,
        int prazoDias,
        LocalDateTime criadaEm) {

    public static CotacaoResponse de(CotacaoFrete c) {
        return new CotacaoResponse(c.getId(), c.getTipoFrete(), c.getCep(), c.getCidade(),
                c.getUf(), c.getPesoKg(), c.getValor(), c.getPrazoDias(), c.getCriadaEm());
    }
}

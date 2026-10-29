package br.com.freteapi.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

@Component("sedex")
public class FreteSedex implements EstrategiaFrete {

    private static final BigDecimal TAXA_FIXA = new BigDecimal("15.00");
    private static final BigDecimal VALOR_POR_KG = new BigDecimal("8.00");

    @Override
    public BigDecimal calcularValor(double pesoKg) {
        return TAXA_FIXA.add(VALOR_POR_KG.multiply(BigDecimal.valueOf(pesoKg)))
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int calcularPrazoDias(boolean interestadual) {
        return interestadual ? 3 : 1;
    }

    @Override
    public String descricao() {
        return "Entrega expressa: mais rápida e mais cara";
    }
}

package br.com.freteapi.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

@Component("transportadora")
public class FreteTransportadora implements EstrategiaFrete {

    private static final BigDecimal TAXA_FIXA = new BigDecimal("30.00");
    private static final BigDecimal VALOR_POR_KG = new BigDecimal("2.50");
    private static final double PESO_MINIMO_KG = 5.0;

    @Override
    public BigDecimal calcularValor(double pesoKg) {
        double pesoCobrado = Math.max(pesoKg, PESO_MINIMO_KG);
        return TAXA_FIXA.add(VALOR_POR_KG.multiply(BigDecimal.valueOf(pesoCobrado)))
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int calcularPrazoDias(boolean interestadual) {
        return interestadual ? 7 : 4;
    }

    @Override
    public String descricao() {
        return "Transportadora: cobra no mínimo 5 kg, compensa para cargas pesadas";
    }
}

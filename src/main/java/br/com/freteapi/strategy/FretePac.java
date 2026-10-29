package br.com.freteapi.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

@Component("pac")
public class FretePac implements EstrategiaFrete {

    private static final BigDecimal TAXA_FIXA = new BigDecimal("10.00");
    private static final BigDecimal VALOR_POR_KG = new BigDecimal("4.00");

    @Override
    public BigDecimal calcularValor(double pesoKg) {
        return TAXA_FIXA.add(VALOR_POR_KG.multiply(BigDecimal.valueOf(pesoKg)))
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int calcularPrazoDias(boolean interestadual) {
        return interestadual ? 9 : 5;
    }

    @Override
    public String descricao() {
        return "Entrega econômica: mais barata e mais lenta";
    }
}

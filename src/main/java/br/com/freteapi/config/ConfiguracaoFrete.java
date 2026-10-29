package br.com.freteapi.config;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * SINGLETON
 *
 * Todo @Component é singleton por padrão no Spring: existe uma única instância
 * desta classe na aplicação, compartilhada por quem precisar dela.
 * Não há construtor privado nem getInstancia(), porque o contêiner do Spring
 * cuida disso. Por ser compartilhada, ela é imutável (campos final).
 */
@Component
public class ConfiguracaoFrete {

    private final String origemUf;
    private final BigDecimal taxaInterestadual;

    public ConfiguracaoFrete(@Value("${frete.origem-uf}") String origemUf,
                             @Value("${frete.taxa-interestadual}") BigDecimal taxaInterestadual) {
        this.origemUf = origemUf;
        this.taxaInterestadual = taxaInterestadual;
    }

    public boolean isInterestadual(String ufDestino) {
        return !origemUf.equalsIgnoreCase(ufDestino);
    }

    public BigDecimal aplicarTaxaInterestadual(BigDecimal valor, boolean interestadual) {
        if (!interestadual) {
            return valor;
        }
        return valor.multiply(taxaInterestadual).setScale(2, RoundingMode.HALF_UP);
    }

    public String getOrigemUf() {
        return origemUf;
    }

    public BigDecimal getTaxaInterestadual() {
        return taxaInterestadual;
    }
}

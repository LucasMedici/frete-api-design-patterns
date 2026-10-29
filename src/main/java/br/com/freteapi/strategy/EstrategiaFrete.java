package br.com.freteapi.strategy;

import java.math.BigDecimal;

/**
 * STRATEGY
 *
 * Contrato comum a todas as formas de calcular frete.
 * Cada implementação é um @Component com nome próprio ("sedex", "pac"...),
 * e o Spring injeta todas num Map<String, EstrategiaFrete>.
 * Para criar um novo tipo de frete, basta criar uma nova classe: nada mais muda.
 */
public interface EstrategiaFrete {

    BigDecimal calcularValor(double pesoKg);

    int calcularPrazoDias(boolean interestadual);

    String descricao();
}

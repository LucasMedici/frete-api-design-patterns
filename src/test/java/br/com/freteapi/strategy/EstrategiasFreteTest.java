package br.com.freteapi.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EstrategiasFreteTest {

    @Test
    void sedexCobraTaxaFixaMaisValorPorKg() {
        assertThat(new FreteSedex().calcularValor(2)).isEqualByComparingTo("31.00");
    }

    @Test
    void pacEhMaisBaratoQueSedexParaPacotesLeves() {
        assertThat(new FretePac().calcularValor(2)).isEqualByComparingTo("18.00");
    }

    @Test
    void transportadoraCobraPesoMinimoDe5Kg() {
        assertThat(new FreteTransportadora().calcularValor(2)).isEqualByComparingTo("42.50");
    }

    @Test
    void transportadoraCompensaParaCargasPesadas() {
        assertThat(new FreteTransportadora().calcularValor(20)).isEqualByComparingTo("80.00");
        assertThat(new FreteSedex().calcularValor(20)).isEqualByComparingTo("175.00");
    }

    @Test
    void prazoAumentaQuandoEhInterestadual() {
        assertThat(new FreteSedex().calcularPrazoDias(false)).isEqualTo(1);
        assertThat(new FreteSedex().calcularPrazoDias(true)).isEqualTo(3);
    }
}

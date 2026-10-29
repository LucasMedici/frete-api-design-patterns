package br.com.freteapi.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.freteapi.config.ConfiguracaoFrete;
import br.com.freteapi.dto.ComparacaoResponse;
import br.com.freteapi.dto.CotacaoRequest;
import br.com.freteapi.dto.CotacaoResponse;
import br.com.freteapi.exception.RegraNegocioException;
import br.com.freteapi.model.CotacaoFrete;
import br.com.freteapi.model.Endereco;
import br.com.freteapi.repository.CotacaoFreteRepository;
import br.com.freteapi.service.ViaCepService;
import br.com.freteapi.strategy.FretePac;
import br.com.freteapi.strategy.FreteSedex;
import br.com.freteapi.strategy.FreteTransportadora;

class FreteFacadeTest {

    private static final Endereco SAO_PAULO =
            new Endereco("01001-000", "Praça da Sé", "Sé", "São Paulo", "SP", null);
    private static final Endereco RIO =
            new Endereco("20040-020", "Praça Pio X", "Centro", "Rio de Janeiro", "RJ", null);

    private ViaCepService viaCepService;
    private FreteFacade facade;

    @BeforeEach
    void setUp() {
        viaCepService = mock(ViaCepService.class);
        CotacaoFreteRepository repository = mock(CotacaoFreteRepository.class);
        when(repository.save(any(CotacaoFrete.class))).thenAnswer(inv -> inv.getArgument(0));

        facade = new FreteFacade(
                Map.of("sedex", new FreteSedex(), "pac", new FretePac(), "transportadora", new FreteTransportadora()),
                viaCepService,
                repository,
                new ConfiguracaoFrete("SP", new BigDecimal("1.30")));
    }

    @Test
    void cotaFreteDentroDoEstadoSemTaxa() {
        when(viaCepService.buscar("01001000")).thenReturn(SAO_PAULO);

        CotacaoResponse resposta = facade.cotar(new CotacaoRequest("01001-000", 2.0, "sedex"));

        assertThat(resposta.valor()).isEqualByComparingTo("31.00");
        assertThat(resposta.prazoDias()).isEqualTo(1);
        assertThat(resposta.cidade()).isEqualTo("São Paulo");
    }

    @Test
    void aplicaTaxaInterestadual() {
        when(viaCepService.buscar("20040020")).thenReturn(RIO);

        CotacaoResponse resposta = facade.cotar(new CotacaoRequest("20040020", 2.0, "SEDEX"));

        assertThat(resposta.valor()).isEqualByComparingTo("40.30");
        assertThat(resposta.prazoDias()).isEqualTo(3);
    }

    @Test
    void tipoInvalidoFalhaAntesDeConsultarOCep() {
        assertThatThrownBy(() -> facade.cotar(new CotacaoRequest("01001000", 2.0, "drone")))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("drone");

        verify(viaCepService, never()).buscar(anyString());
    }

    @Test
    void cepComTamanhoErradoEhRejeitado() {
        assertThatThrownBy(() -> facade.comparar("123", 2.0))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void comparacaoVemOrdenadaDoMaisBaratoAoMaisCaro() {
        when(viaCepService.buscar("01001000")).thenReturn(SAO_PAULO);

        ComparacaoResponse resposta = facade.comparar("01001000", 2.0);

        assertThat(resposta.opcoes()).extracting("tipoFrete")
                .containsExactly("pac", "sedex", "transportadora");
        assertThat(resposta.interestadual()).isFalse();
    }
}

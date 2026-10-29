package br.com.freteapi.facade;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import br.com.freteapi.config.ConfiguracaoFrete;
import br.com.freteapi.dto.ComparacaoResponse;
import br.com.freteapi.dto.CotacaoRequest;
import br.com.freteapi.dto.CotacaoResponse;
import br.com.freteapi.dto.OpcaoFreteResponse;
import br.com.freteapi.dto.TipoFreteResponse;
import br.com.freteapi.exception.RegraNegocioException;
import br.com.freteapi.model.CotacaoFrete;
import br.com.freteapi.model.Endereco;
import br.com.freteapi.repository.CotacaoFreteRepository;
import br.com.freteapi.service.ViaCepService;
import br.com.freteapi.strategy.EstrategiaFrete;

/**
 * FACADE
 *
 * Esconde a complexidade de uma cotação atrás de métodos simples.
 * Uma cotação envolve: validar o CEP, consultar o ViaCEP, escolher a estratégia,
 * aplicar a taxa interestadual (regra do singleton de configuração) e salvar no banco.
 * O controller não conhece nada disso: só chama freteFacade.cotar(...).
 */
@Component
public class FreteFacade {

    private static final double PESO_MAXIMO_KG = 100.0;

    private final Map<String, EstrategiaFrete> estrategias;
    private final ViaCepService viaCepService;
    private final CotacaoFreteRepository repository;
    private final ConfiguracaoFrete configuracao;

    public FreteFacade(Map<String, EstrategiaFrete> estrategias,
                       ViaCepService viaCepService,
                       CotacaoFreteRepository repository,
                       ConfiguracaoFrete configuracao) {
        this.estrategias = estrategias;
        this.viaCepService = viaCepService;
        this.repository = repository;
        this.configuracao = configuracao;
    }

    public CotacaoResponse cotar(CotacaoRequest request) {
        String tipo = request.tipoFrete().trim().toLowerCase();
        EstrategiaFrete estrategia = buscarEstrategia(tipo);
        validarPeso(request.pesoKg());
        String cep = normalizarCep(request.cep());

        Endereco destino = viaCepService.buscar(cep);
        boolean interestadual = configuracao.isInterestadual(destino.uf());

        BigDecimal valor = configuracao.aplicarTaxaInterestadual(
                estrategia.calcularValor(request.pesoKg()), interestadual);
        int prazo = estrategia.calcularPrazoDias(interestadual);

        CotacaoFrete cotacao = new CotacaoFrete(cep, destino.localidade(), destino.uf(),
                request.pesoKg(), tipo, valor, prazo);

        return CotacaoResponse.de(repository.save(cotacao));
    }

    public ComparacaoResponse comparar(String cep, double pesoKg) {
        validarPeso(pesoKg);
        Endereco destino = viaCepService.buscar(normalizarCep(cep));
        boolean interestadual = configuracao.isInterestadual(destino.uf());

        List<OpcaoFreteResponse> opcoes = estrategias.entrySet().stream()
                .map(e -> new OpcaoFreteResponse(
                        e.getKey(),
                        e.getValue().descricao(),
                        configuracao.aplicarTaxaInterestadual(e.getValue().calcularValor(pesoKg), interestadual),
                        e.getValue().calcularPrazoDias(interestadual)))
                .sorted(Comparator.comparing(OpcaoFreteResponse::valor))
                .toList();

        return new ComparacaoResponse(destino.localidade(), destino.uf(), interestadual, opcoes);
    }

    public List<CotacaoResponse> historico() {
        return repository.findAllByOrderByCriadaEmDesc().stream()
                .map(CotacaoResponse::de)
                .toList();
    }

    public List<TipoFreteResponse> tiposDisponiveis() {
        return estrategias.entrySet().stream()
                .map(e -> new TipoFreteResponse(e.getKey(), e.getValue().descricao()))
                .sorted(Comparator.comparing(TipoFreteResponse::tipo))
                .toList();
    }

    private EstrategiaFrete buscarEstrategia(String tipo) {
        EstrategiaFrete estrategia = estrategias.get(tipo);
        if (estrategia == null) {
            throw new RegraNegocioException("Tipo de frete inválido: '" + tipo
                    + "'. Tipos disponíveis: " + estrategias.keySet());
        }
        return estrategia;
    }

    private void validarPeso(Double pesoKg) {
        if (pesoKg == null || pesoKg <= 0 || pesoKg > PESO_MAXIMO_KG) {
            throw new RegraNegocioException("Peso deve ser maior que 0 e no máximo " + PESO_MAXIMO_KG + " kg");
        }
    }

    private String normalizarCep(String cep) {
        String digitos = cep == null ? "" : cep.replaceAll("\\D", "");
        if (digitos.length() != 8) {
            throw new RegraNegocioException("CEP deve ter 8 dígitos (ex: 01001-000)");
        }
        return digitos;
    }
}

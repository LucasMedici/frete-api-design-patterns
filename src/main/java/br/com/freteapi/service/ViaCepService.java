package br.com.freteapi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import br.com.freteapi.exception.CepNaoEncontradoException;
import br.com.freteapi.exception.ServicoExternoException;
import br.com.freteapi.model.Endereco;

/** Consulta endereços na API pública do ViaCEP (https://viacep.com.br). */
@Service
public class ViaCepService {

    private final RestClient restClient;

    public ViaCepService(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://viacep.com.br/ws").build();
    }

    public Endereco buscar(String cep) {
        Endereco endereco;
        try {
            endereco = restClient.get()
                    .uri("/{cep}/json/", cep)
                    .retrieve()
                    .body(Endereco.class);
        } catch (RestClientException e) {
            throw new ServicoExternoException("Não foi possível consultar o ViaCEP no momento", e);
        }

        if (endereco == null || Boolean.TRUE.equals(endereco.erro())) {
            throw new CepNaoEncontradoException(cep);
        }
        return endereco;
    }
}

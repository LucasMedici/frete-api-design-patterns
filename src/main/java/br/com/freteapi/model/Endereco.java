package br.com.freteapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Endereço retornado pela API pública do ViaCEP. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Endereco(
        String cep,
        String logradouro,
        String bairro,
        String localidade,
        String uf,
        Boolean erro) {
}

package br.com.freteapi.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record CotacaoRequest(
        @NotBlank(message = "CEP é obrigatório")
        @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve ter 8 dígitos (ex: 01001-000)")
        String cep,

        @NotNull(message = "Peso é obrigatório")
        @Positive(message = "Peso deve ser maior que zero")
        @DecimalMax(value = "100.0", message = "Peso máximo é 100 kg")
        Double pesoKg,

        @NotBlank(message = "Tipo de frete é obrigatório (veja GET /fretes/tipos)")
        String tipoFrete) {
}

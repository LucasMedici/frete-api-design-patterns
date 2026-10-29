package br.com.freteapi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.freteapi.dto.ComparacaoResponse;
import br.com.freteapi.dto.CotacaoRequest;
import br.com.freteapi.dto.CotacaoResponse;
import br.com.freteapi.dto.TipoFreteResponse;
import br.com.freteapi.facade.FreteFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/fretes")
@Tag(name = "Fretes", description = "Cotação, comparação e histórico de fretes")
public class FreteController {

    private final FreteFacade freteFacade;

    public FreteController(FreteFacade freteFacade) {
        this.freteFacade = freteFacade;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cota um frete e salva no histórico")
    public CotacaoResponse cotar(@RequestBody @Valid CotacaoRequest request) {
        return freteFacade.cotar(request);
    }

    @GetMapping("/comparar")
    @Operation(summary = "Simula todos os tipos de frete para um CEP e peso, do mais barato ao mais caro")
    public ComparacaoResponse comparar(@RequestParam String cep, @RequestParam double pesoKg) {
        return freteFacade.comparar(cep, pesoKg);
    }

    @GetMapping
    @Operation(summary = "Lista o histórico de cotações, das mais recentes para as mais antigas")
    public List<CotacaoResponse> historico() {
        return freteFacade.historico();
    }

    @GetMapping("/tipos")
    @Operation(summary = "Lista os tipos de frete disponíveis")
    public List<TipoFreteResponse> tipos() {
        return freteFacade.tiposDisponiveis();
    }
}

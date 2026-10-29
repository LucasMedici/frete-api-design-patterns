package br.com.freteapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.freteapi.model.CotacaoFrete;

public interface CotacaoFreteRepository extends JpaRepository<CotacaoFrete, Long> {

    List<CotacaoFrete> findAllByOrderByCriadaEmDesc();
}

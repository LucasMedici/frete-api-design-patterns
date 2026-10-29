package br.com.freteapi.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cotacoes_frete")
public class CotacaoFrete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 8)
    private String cep;

    private String cidade;

    @Column(length = 2)
    private String uf;

    private double pesoKg;

    @Column(nullable = false)
    private String tipoFrete;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    private int prazoDias;

    private LocalDateTime criadaEm;

    protected CotacaoFrete() {
        // exigido pelo JPA
    }

    public CotacaoFrete(String cep, String cidade, String uf, double pesoKg,
                        String tipoFrete, BigDecimal valor, int prazoDias) {
        this.cep = cep;
        this.cidade = cidade;
        this.uf = uf;
        this.pesoKg = pesoKg;
        this.tipoFrete = tipoFrete;
        this.valor = valor;
        this.prazoDias = prazoDias;
        this.criadaEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getCep() { return cep; }
    public String getCidade() { return cidade; }
    public String getUf() { return uf; }
    public double getPesoKg() { return pesoKg; }
    public String getTipoFrete() { return tipoFrete; }
    public BigDecimal getValor() { return valor; }
    public int getPrazoDias() { return prazoDias; }
    public LocalDateTime getCriadaEm() { return criadaEm; }
}

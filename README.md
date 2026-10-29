# 🚚 Frete API: Padrões de Projeto com Spring Boot

API REST de cotação de frete desenvolvida como desafio final do módulo de **Padrões de Projeto (Design Patterns)**.
O usuário informa o CEP de destino, o peso e o tipo de entrega, e a API consulta o endereço no [ViaCEP](https://viacep.com.br), calcula valor e prazo, e salva a cotação no histórico.

O foco do projeto é mostrar, de forma prática, três padrões de projeto: **Singleton**, **Strategy** e **Facade**.

## 🧩 Padrões aplicados

### Singleton: `ConfiguracaoFrete`

No Spring, todo bean (`@Component`, `@Service`...) é **singleton por padrão**: o contêiner cria uma única instância e a compartilha com toda a aplicação. A classe `ConfiguracaoFrete` guarda as regras globais (UF de origem e taxa interestadual, lidas do `application.properties`) e é a mesma instância em todos os lugares que a usam.

Diferente do Singleton "clássico" em Java puro, não é preciso construtor privado nem método `getInstancia()`: o Spring cuida do ciclo de vida. Como a instância é compartilhada, ela é **imutável** (campos `final`), o que evita problemas com várias requisições ao mesmo tempo.

### Strategy: `EstrategiaFrete`

Existem várias formas de calcular um frete, e cada uma é uma **estratégia** que implementa a mesma interface:

| Estratégia | Bean | Regra |
|---|---|---|
| `FreteSedex` | `sedex` | R$ 15 + R$ 8/kg, entrega rápida |
| `FretePac` | `pac` | R$ 10 + R$ 4/kg, entrega econômica |
| `FreteTransportadora` | `transportadora` | R$ 30 + R$ 2,50/kg, cobra no mínimo 5 kg |

O Spring injeta todas as estratégias num `Map<String, EstrategiaFrete>`, onde a chave é o nome do bean. A escolha acontece em tempo de execução, sem nenhum `if/else`:

```java
EstrategiaFrete estrategia = estrategias.get(request.tipoFrete());
BigDecimal valor = estrategia.calcularValor(request.pesoKg());
```

**Para adicionar um novo tipo de frete**, basta criar uma classe nova. Nada mais no projeto precisa mudar:

```java
@Component("motoboy")
public class FreteMotoboy implements EstrategiaFrete {
    public BigDecimal calcularValor(double pesoKg) { return new BigDecimal("12.00"); }
    public int calcularPrazoDias(boolean interestadual) { return 0; }
    public String descricao() { return "Entrega no mesmo dia"; }
}
```

### Facade: `FreteFacade`

Uma cotação envolve vários passos: validar o CEP, consultar o ViaCEP, escolher a estratégia, aplicar a taxa interestadual e salvar no banco. A `FreteFacade` esconde toda essa orquestração atrás de métodos simples, e o controller fica com uma linha por endpoint:

```java
@PostMapping
public CotacaoResponse cotar(@RequestBody @Valid CotacaoRequest request) {
    return freteFacade.cotar(request);
}
```

```
Controller ──► FreteFacade ──┬──► ViaCepService (API externa)
                             ├──► EstrategiaFrete (Sedex / PAC / Transportadora)
                             ├──► ConfiguracaoFrete (singleton com as regras)
                             └──► CotacaoFreteRepository (banco H2)
```

## 🛠️ Tecnologias

- Java 17
- Spring Boot 3 (Web, Data JPA, Validation)
- H2 Database (banco em memória)
- RestClient para consumir a API do ViaCEP
- SpringDoc OpenAPI (Swagger UI)
- JUnit 5, Mockito e AssertJ

## ▶️ Como rodar

Pré-requisito: **Java 17+**. Não é preciso instalar o Maven: o projeto inclui o Maven Wrapper (`mvnw`). Também dá para abrir direto no IntelliJ / VS Code.

```bash
./mvnw spring-boot:run
```

No Windows, use `mvnw.cmd` no lugar de `./mvnw`.

Depois acesse:

- Swagger UI: http://localhost:8080/swagger-ui.html
- Console do H2: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:fretedb`, usuário `sa`, sem senha)

Para rodar os testes:

```bash
./mvnw test
```

## 📡 Endpoints

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/fretes` | Cota um frete e salva no histórico (retorna `201 Created`) |
| `GET` | `/fretes/comparar?cep=&pesoKg=` | Simula todos os tipos, do mais barato ao mais caro |
| `GET` | `/fretes` | Histórico de cotações |
| `GET` | `/fretes/tipos` | Tipos de frete disponíveis |

### Exemplo: cotar um frete

```bash
curl -X POST http://localhost:8080/fretes \
  -H "Content-Type: application/json" \
  -d '{"cep": "20040-020", "pesoKg": 2, "tipoFrete": "sedex"}'
```

```json
{
  "id": 1,
  "tipoFrete": "sedex",
  "cep": "20040020",
  "cidade": "Rio de Janeiro",
  "uf": "RJ",
  "pesoKg": 2.0,
  "valor": 40.30,
  "prazoDias": 3,
  "criadaEm": "2026-10-29T14:32:10"
}
```

Como a origem configurada é SP e o destino é RJ, a taxa interestadual de 30% foi aplicada (R$ 31,00 → R$ 40,30).

### Exemplo: comparar todas as opções

```bash
curl "http://localhost:8080/fretes/comparar?cep=01001000&pesoKg=20"
```

Com 20 kg, a transportadora (R$ 80,00) sai bem mais barata que o Sedex (R$ 175,00), o que mostra na prática por que cada estratégia existe.

### Tratamento de erros

Os erros seguem o padrão **Problem Details (RFC 7807)**:

| Situação | Status |
|---|---|
| Campos inválidos, CEP mal formatado ou tipo de frete inexistente | `400` |
| CEP não encontrado no ViaCEP | `404` |
| ViaCEP fora do ar | `502` |

## 📁 Estrutura

```
src/main/java/br/com/freteapi
├── config/        → ConfiguracaoFrete (Singleton)
├── strategy/      → EstrategiaFrete + implementações (Strategy)
├── facade/        → FreteFacade (Facade)
├── controller/    → endpoints REST
├── service/       → integração com o ViaCEP
├── repository/    → acesso ao banco
├── model/         → entidade e endereço
├── dto/           → objetos de entrada e saída
└── exception/     → exceções e tratamento global de erros
```
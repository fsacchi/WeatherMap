<h1 align="center">
  <br>
  🌤️ WeatherMap
  <br>
</h1>

<p align="center">
  Aplicativo Android de clima em tempo real com mapa interativo, busca de cidades e histórico de pesquisas.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-26%2B-brightgreen?logo=android" />
  <img src="https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-2024.09.00-4285F4?logo=jetpackcompose" />
  <img src="https://img.shields.io/badge/OpenWeatherMap-API-orange?logo=openweathermap" />
  <img src="https://img.shields.io/badge/Architecture-MVVM%20%2B%20MVI%20%2B%20Clean-blueviolet" />
</p>

---

## Sobre o Projeto

**WeatherMap** é um aplicativo Android nativo desenvolvido inteiramente com **Jetpack Compose** que permite ao usuário consultar o clima atual e a previsão dos próximos dias de qualquer cidade ou localização no mapa, de forma intuitiva e visualmente agradável.

### Funcionalidades

- **Mapa interativo** — Toque em qualquer ponto do mapa para consultar o clima daquele local
- **Busca por cidade** — Campo de pesquisa com autocomplete em tempo real
- **Clima atual** — Temperatura, sensação térmica, mín/máx, umidade, vento, pressão e horários de nascer/pôr do sol
- **Previsão de 5 dias** — Itens de forecast com gráfico de probabilidade de precipitação
- **Histórico** — Todas as buscas salvas localmente com swipe-to-delete e remoção total
- **Localização GPS** — Centraliza o mapa e busca o clima da posição atual automaticamente
- **Compartilhamento** — Compartilha o clima atual via screenshot
- **Background dinâmico** — Gradiente de cores que muda conforme a condição do tempo

---

### Screenshots

<p float="left">
  <img width="25%" height="45%" src="https://github.com/fsacchi/WeatherMap/blob/main/screenshots/screen1.jpeg" />
  <img width="25%" height="45%" src="https://github.com/fsacchi/WeatherMap/blob/main/screenshots/screen2.jpeg" />
  <img width="25%" height="45%" src="https://github.com/fsacchi/WeatherMap/blob/main/screenshots/screen3.jpeg" />
  <img width="25%" height="45%" src="https://github.com/fsacchi/WeatherMap/blob/main/screenshots/screen4.jpeg" />
  <img width="25%" height="45%" src="https://github.com/fsacchi/WeatherMap/blob/main/screenshots/screen5.jpeg" />
</p>

---

## Tecnologias e Bibliotecas

### Linguagem & SDK
| Tecnologia | Versão |
|---|---|
| Kotlin | 2.0.21 |
| Android minSdk | 26 (Android 8.0) |
| Android targetSdk | 36 |
| Java Toolchain | 11 |

### UI
| Biblioteca | Uso |
|---|---|
| [Jetpack Compose](https://developer.android.com/jetpack/compose) | Framework de UI declarativa (100% Compose, sem XML) |
| Material3 | Design system e componentes visuais |
| Coil Compose | Carregamento assíncrono de imagens (ícones do clima) |

### Mapa
| Biblioteca | Uso |
|---|---|
| [osmdroid 6.1.20](https://github.com/osmdroid/osmdroid) | Mapa interativo baseado no OpenStreetMap (Tile source: Mapnik) |

### Rede
| Biblioteca | Uso |
|---|---|
| [Retrofit 2.11.0](https://square.github.io/retrofit/) | Cliente HTTP para consumo da API |
| OkHttp 4.12.0 | Interceptors (API Key, logging) e configuração de timeouts |
| Kotlinx Serialization 1.7.3 | Serialização/desserialização de JSON |

### Persistência
| Biblioteca | Uso |
|---|---|
| [Room 2.7.1](https://developer.android.com/training/data-storage/room) | Banco de dados local SQLite (histórico de buscas) |
| KSP 2.0.21 | Geração de código para Room em tempo de compilação |

### Injeção de Dependências
| Biblioteca | Uso |
|---|---|
| [Koin 4.0.2](https://insert-koin.io/) | Service locator e DI para Android e Compose |

### Navegação
| Biblioteca | Uso |
|---|---|
| Navigation Compose 2.9.0 | Navegação type-safe entre telas com passagem de parâmetros |

### Localização
| Biblioteca | Uso |
|---|---|
| Google Play Services Location 21.3.0 | GPS e localização em tempo real |

### Logs & Debug
| Biblioteca | Uso |
|---|---|
| Timber 5.0.1 | Logging estruturado para debug |

### Testes
| Biblioteca | Uso |
|---|---|
| [JUnit 5 (Jupiter) 5.11.0](https://junit.org/junit5/) | Framework de testes unitários modernos |
| [MockK 1.13.12](https://mockk.io/) | Mocking idiomático para Kotlin |
| [Turbine 1.2.0](https://github.com/cashapp/turbine) | Testes de `Flow` e `StateFlow` |
| Kotlinx Coroutines Test 1.9.0 | Controle de coroutines em testes (TestDispatcher) |
| Compose UI Test (JUnit4) | Renderização e asserções de estado em composables (`composeTestRule`) |

---

## Arquitetura

O projeto segue os princípios da **Clean Architecture** combinada com os padrões **MVVM** e **MVI**, garantindo separação clara de responsabilidades, testabilidade e escalabilidade.

```
app/
└── com.fsacchi.weathermap/
    ├── core/                   # Utilitários e extensões gerais
    │   ├── extensions/         # Extensões de Context, Date, etc.
    │   └── util/               # Result<T>, SafeRun wrapper
    │
    ├── data/                   # Camada de dados
    │   ├── local/              # Room: Database, Entities, DAOs
    │   ├── remote/             # Retrofit: APIs, DTOs, Interceptors
    │   └── repository/         # Implementações dos repositórios
    │
    ├── domain/                 # Camada de domínio (regras de negócio)
    │   ├── model/              # Modelos de domínio
    │   ├── repository/         # Interfaces dos repositórios
    │   └── usecase/            # Casos de uso (single responsibility)
    │
    ├── di/                     # Módulos Koin
    │
    └── presentation/           # Camada de apresentação
        ├── map/                # Tela do mapa + ViewModel
        ├── detail/             # Tela de detalhes do clima + ViewModel
        ├── history/            # Tela de histórico + ViewModel
        └── navigation/         # Configuração de rotas
```

### Camadas

| Camada | Responsabilidade |
|---|---|
| **Presentation** | ViewModels com `StateFlow`, Actions (intents MVI), Compose screens |
| **Domain** | Use cases, interfaces de repositório, modelos de domínio |
| **Data** | Implementação dos repositórios, DTOs, Room, Retrofit |

### Gerenciamento de Estado

- `sealed class Result<T>` — `Success`, `Error` e `Loading` para encapsular resultados
- `StateFlow<UiState>` — estado reativo imutável consumido pelas telas
- `onAction(action: XAction)` — ponto único de entrada de eventos nas ViewModels

---

## API — OpenWeatherMap

O app consome dois endpoints da [OpenWeatherMap API](https://openweathermap.org/api):

| Endpoint | Uso |
|---|---|
| `GET /data/2.5/weather` | Clima atual por coordenadas ou nome de cidade |
| `GET /data/2.5/forecast` | Previsão dos próximos 5 dias (16 itens) |
| `GET /geo/1.0/direct` | Geocoding para busca de cidade por nome |

Parâmetros padrão: `units=metric` (Celsius) · `lang=pt_br`

---

## Testes Unitários

Cobertura abrangente de **use cases** e **ViewModels** com JUnit 5 + MockK + Turbine.

### Use Cases testados

| Teste | O que valida |
|---|---|
| `GetWeatherByLocationUseCaseTest` | Busca de clima por coordenadas |
| `GetWeatherByCityUseCaseTest` | Busca de clima por nome de cidade |
| `GetForecastUseCaseTest` | Busca de previsão de 5 dias |
| `SearchCityUseCaseTest` | Autocomplete de cidades |
| `GetWeatherHistoryUseCaseTest` | Recuperação do histórico local |
| `SaveWeatherHistoryUseCaseTest` | Persistência de novas buscas |

### ViewModels testados

| Teste | O que valida |
|---|---|
| `MapViewModelTest` | Estado do mapa, debounce de busca, fallback de GPS, salvamento no histórico |
| `WeatherDetailViewModelTest` | Carregamento de detalhes e previsão, estados de loading/error |
| `HistoryViewModelTest` | Listagem, deleção individual e remoção completa do histórico |

---

## Testes de UI (Compose)

Testes instrumentados que verificam o que o usuário **realmente vê na tela**, renderizando composables com estados controlados — sem ViewModel, sem rede, sem banco.

---
## Licença

```
MIT License — sinta-se livre para usar, modificar e distribuir.
```

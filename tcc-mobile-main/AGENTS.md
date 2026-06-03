# AGENTS.md

Este arquivo orienta quem for contribuir com o projeto. O foco aqui e explicar a arquitetura atual, como as partes se conectam e onde mexer sem quebrar o fluxo principal.

## 1. Visao geral

O projeto e um app Kotlin Multiplatform com um unico modulo compartilhado, `composeApp`, e um wrapper nativo iOS em `iosApp`.

Hoje, a arquitetura real e esta:

- `composeApp/src/commonMain`: UI Compose compartilhada, logica de jogo, modelos e processamento de telemetria.
- `composeApp/src/androidMain`: ponto de entrada Android, runtime de telemetria, sensores, localizacao e service em foreground.
- `composeApp/src/iosMain`: ponto de entrada iOS e implementacoes `actual` minimas.
- `iosApp`: shell SwiftUI que embala o `MainViewController()` gerado pelo modulo Kotlin.

O app atual e essencialmente um jogo de corrida/fuga com um pipeline de telemetria. O Android coleta sinais do dispositivo, transforma isso em metricas de movimento e alimenta a simulacao do jogo. A cena visual do jogo roda com KorGE dentro da UI Compose.

## 2. Mapa do repositorio

### Modulos Gradle

- `settings.gradle.kts`: registra apenas `:composeApp`.
- `build.gradle.kts`: configura plugins no root.
- `composeApp/build.gradle.kts`: define os targets Android e iOS, dependencias Compose Multiplatform, Lifecycle, KorGE e Play Services Location.

### Pastas principais

- `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/App.kt`
  - composable raiz da aplicacao.
- `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/game`
  - jogo, controle de sessao, cena KorGE, entidades e calculos.
- `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/game/telemetry`
  - modelos e casos de uso que transformam sensores em metricas de gameplay.
- `composeApp/src/androidMain/kotlin/com/pedroaba/tccmobile/telemetry`
  - implementacao Android da coleta de telemetria.
- `composeApp/src/commonMain/composeResources/files/game_assets`
  - sprites e backgrounds do jogo.
- `iosApp/iosApp`
  - app SwiftUI que hospeda o modulo Kotlin.

## 3. Arquitetura por camadas

Pense no app em 4 camadas conectadas.

### Camada 1: Shell da plataforma

Responsavel por iniciar a UI e, no Android, plugar o runtime nativo.

- Android: `MainActivity`
- iOS: `MainViewController.kt` + `iosApp/iosApp/ContentView.swift`

No Android, `MainActivity`:

- obtem um singleton de `AndroidTelemetryRuntime` via `TelemetryRuntimeProvider`;
- controla permissoes de localizacao e notificacao;
- passa `telemetryStateFlow` e callbacks de sessao para `App(...)`;
- inicia e encerra `TelemetryForegroundService`.

No iOS, o fluxo hoje e bem mais fino:

- `MainViewController()` apenas monta `App()`;
- nao existe runtime de telemetria nativo equivalente ao Android;
- a view de jogo em iOS ainda e placeholder, nao um bridge KorGE funcional.

### Camada 2: UI Compose

Arquivo central: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/App.kt`

Essa camada:

- renderiza a tela principal;
- observa `TelemetryState`;
- instancia e observa `GameController`;
- envia `latestEscapeMetrics` para o jogo;
- sincroniza status da sessao de telemetria com a sessao do jogo;
- embute a area visual do jogo por meio de `KorgeGameView(...)`.

Em termos praticos, `App.kt` e o orquestrador entre:

- estado de telemetria vindo do Android;
- estado do jogo vindo do `GameController`;
- controles da sessao expostos na interface.

### Camada 3: Dominio do jogo

Arquivos principais:

- `game/GameController.kt`
- `game/logic/RunCalculator.kt`
- `game/models/*`
- `game/visual/DistanceVisualMapper.kt`
- `game/scenes/MainScene.kt`
- `game/entities/*`

Responsabilidades:

- `GameController`
  - estado de sessao do jogo;
  - snapshot atual do gameplay;
  - aplicacao de `EscapeMetrics`;
  - regras de start/stop;
  - coordena o que a cena KorGE vai refletir.

- `RunCalculator`
  - converte sinais em `GameSnapshot`;
  - define velocidade do corredor, pressao da horda, risco e resultado.

- `MainScene`
  - monta os objetos KorGE;
  - atualiza fundo, corredor e horda a cada frame;
  - consome `controller.snapshot`.

Em resumo: o gameplay nao fala direto com sensores Android. Ele recebe um estado abstraido e trabalha em cima disso.

### Camada 4: Pipeline de telemetria

Essa e a parte mais importante para quem for evoluir o projeto.

#### 4.1 Entrada dos sinais

No Android, os sinais entram por:

- `FusedLocationTrackingService`
  - coleta GPS/localizacao via `FusedLocationProviderClient`;
- `AndroidMotionSensorService`
  - coleta aceleracao via sensores do dispositivo;
- `WearTelemetryBridge`
  - ponte para sinais de wearable.

Hoje, o runtime usa `NoOpWearTelemetryBridge`, entao a integracao com relogio esta preparada na arquitetura, mas nao implementada de fato.

#### 4.2 Runtime e repositorio

Arquivos principais:

- `telemetry/service/AndroidTelemetryRuntime.kt`
- `telemetry/data/DefaultTelemetryRepository.kt`
- `telemetry/data/TelemetryRepository.kt`

Fluxo:

1. `AndroidTelemetryRuntime.create(context)` monta as dependencias Android.
2. `DefaultTelemetryRepository` centraliza o estado de telemetria.
3. O repositorio expoe `StateFlow<TelemetryState>`.
4. `MainActivity` injeta esse flow no composable `App(...)`.

`DefaultTelemetryRepository` e o centro do pipeline. Ele:

- inicia e interrompe coleta;
- acompanha disponibilidade de sensores/permissoes;
- agrega dados de localizacao, aceleracao e biofeedback;
- escolhe a estrategia (`MOVEMENT_ONLY` ou `BPM_AND_MOVEMENT`);
- gera `TelemetrySample`;
- deriva `EscapeMetrics`;
- publica tudo em `TelemetryState`.

#### 4.3 Processamento dos sinais

Arquivos principais em `commonMain`:

- `game/telemetry/usecase/MovementTelemetryProcessor.kt`
- `game/telemetry/usecase/ComputeEscapeMetricsUseCase.kt`
- `game/telemetry/usecase/SelectTelemetryStrategyUseCase.kt`

Responsabilidades:

- `MovementTelemetryProcessor`
  - filtra ruido;
  - suaviza velocidade e aceleracao;
  - detecta dado stale;
  - produz `TelemetrySample`.

- `SelectTelemetryStrategyUseCase`
  - decide se a sessao usa apenas movimento ou movimento + BPM.

- `ComputeEscapeMetricsUseCase`
  - transforma `TelemetrySample` em `EscapeMetrics`;
  - gera scores normalizados que o gameplay entende.

#### 4.4 Saida para o jogo

O encadeamento principal e este:

1. Android coleta `LocationPoint` e `AccelerationSample`.
2. `DefaultTelemetryRepository` envia isso para `MovementTelemetryProcessor`.
3. O processor gera `TelemetrySample`.
4. `ComputeEscapeMetricsUseCase` converte isso em `EscapeMetrics`.
5. `TelemetryState.latestEscapeMetrics` e atualizado.
6. `App.kt` observa esse estado e chama `gameController.applyEscapeMetrics(...)`.
7. `GameController` recalcula `GameSnapshot`.
8. `MainScene` le o snapshot e atualiza a cena KorGE.

Se voce quebrar esse fluxo, o app pode continuar abrindo, mas o jogo deixa de reagir a telemetria.

## 4. Plataforma por plataforma

### Android

E a plataforma principal hoje.

Pontos importantes:

- `MainActivity` e o entrypoint real do app funcional.
- `TelemetryForegroundService` mantem a sessao viva e atualiza notificacoes.
- `AndroidManifest.xml` declara:
  - permissao de localizacao;
  - permissao de foreground service;
  - permissao de notificacao;
  - service `TelemetryForegroundService`.
- `KorgeGameView.android.kt` integra KorGE via `KorgeAndroidView`.

Quando mexer em Android, verifique impacto em:

- permissoes;
- lifecycle da activity;
- foreground service;
- consumo de `telemetryState`.

### iOS

O suporte existe como casca multiplataforma, mas nao esta no mesmo nivel do Android.

Estado atual:

- `MainViewController.kt` apenas renderiza o Compose root.
- `iosApp` embala esse controller em SwiftUI.
- `KorgeGameView.ios.kt` ainda e placeholder textual.
- nao existe implementacao iOS do runtime de telemetria equivalente ao Android.

Contribuicoes em iOS devem partir desse entendimento: hoje iOS nao e feature parity com Android.

## 5. Contratos e estados importantes

### `TelemetryState`

E o contrato principal entre a camada Android e a UI compartilhada.

Ele concentra:

- sessao (`MovementSession`);
- ultima amostra processada;
- ultima amostra de biofeedback;
- ultimas metricas de escape;
- estrategia escolhida;
- disponibilidade e problemas de telemetria.

### `GameSnapshot`

E o contrato principal entre o `GameController` e a cena do jogo.

Ele concentra:

- distancia;
- pressao da horda;
- risco;
- score de performance;
- velocidades;
- tempo decorrido;
- resultado (`running`, `caught`, `escaped`).

## 6. Regras praticas para contribuir

### Ao mexer em UI Compose

- trate `App.kt` como ponto de orquestracao, nao como lugar para enfiar regra de negocio pesada;
- se surgir logica de calculo, prefira mover para `game/*` ou `game/telemetry/*`;
- preserve o fluxo `TelemetryState -> GameController -> GameSnapshot -> MainScene`.

### Ao mexer no gameplay

- `GameController` concentra o estado do jogo;
- `RunCalculator` concentra o calculo;
- `MainScene` deve refletir estado, nao inventar estado paralelo;
- evite acoplar KorGE direto a APIs Android.

### Ao mexer na telemetria

- mantenha o processamento de sinais em `commonMain` sempre que possivel;
- mantenha coleta de sensores em `androidMain`;
- se introduzir nova fonte de sinal, conecte-a ao `DefaultTelemetryRepository`, nao direto na UI;
- preserve `TelemetryState` como interface publica do pipeline.

### Ao mexer em multiplataforma

- use `expect/actual` apenas quando houver dependencia real de plataforma;
- tudo que puder ficar em `commonMain` deve ficar em `commonMain`;
- nao assuma que um arquivo em `iosMain` tem feature parity com Android.

## 7. Pontos de atencao

### Android e a referencia funcional

Se existir divergencia entre Android e iOS, assuma que o comportamento de referencia hoje esta no Android.

### A ponte de wearable ainda nao existe

Existe interface para wearable, mas o runtime usa `NoOpWearTelemetryBridge`. Se alguem for implementar isso, o ponto de entrada correto e esse contrato, nao um atalho em `App.kt`.

### O jogo depende de metricas, nao de sensores brutos

Esse desacoplamento e importante. Nao passe `Location`, `SensorEvent` ou tipos Android para o dominio compartilhado.

### Existem modelos com nomes parecidos

Ha tipos relacionados a biofeedback em mais de um pacote. Antes de reutilizar ou mover algo, confira o pacote importado para nao misturar modelos de dominio do jogo com modelos da telemetria.

### Cobertura de testes ainda e baixa

O projeto tem `commonTest`, mas a base atual praticamente nao documenta testes relevantes. Em mudancas sensiveis, priorize testes para:

- `MovementTelemetryProcessor`;
- `ComputeEscapeMetricsUseCase`;
- `RunCalculator`;
- qualquer regra nova em `GameController`.

## 8. Comandos uteis

### Build Android

```bash
./gradlew :composeApp:assembleDebug
```

### Compilar Kotlin Android

```bash
./gradlew :composeApp:compileDebugKotlinAndroid
```

### Compilar iOS simulator

```bash
./gradlew :composeApp:compileKotlinIosSimulatorArm64
```

## 9. Como pensar antes de alterar algo

Perguntas que todo contribuidor deveria responder antes de abrir um PR:

1. Estou alterando coleta nativa, processamento compartilhado ou apenas apresentacao?
2. Essa mudanca quebra o contrato de `TelemetryState`?
3. Essa mudanca quebra o contrato de `GameSnapshot`?
4. Estou colocando regra de negocio na camada errada?
5. Essa mudanca impacta apenas Android ou deveria existir uma estrategia equivalente para iOS?

## 10. Resumo operacional

Se voce lembrar de apenas uma coisa, lembre desta cadeia:

`Android sensors/location -> DefaultTelemetryRepository -> TelemetrySample/EscapeMetrics -> App.kt -> GameController -> GameSnapshot -> MainScene/KorGE`

Esse e o eixo da arquitetura atual. A maior parte das contribuicoes relevantes vai tocar algum ponto dessa linha.

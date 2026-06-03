# Design: Reorganização das Telas do Jogo

## Contexto

O arquivo `App.kt` contém aScreen `GameScreen` e seus componentes auxiliares misturados com o composable raiz `App()`. O objetivo é separar aScreen do jogo seguindo o padrão existente em `features/auth/screens/`.

## Estrutura Proposta

```
features/
└── game/
    └── screens/
        ├── GameScreen.kt
        └── components/
            ├── SessionSignalCard.kt
            ├── TelemetryStatusCard.kt
            ├── StatusDisplay.kt
            └── StatusRow.kt
```

## Componentes

### GameScreen.kt
- Screen principal do jogo
- Recebe parameters: `telemetryStateFlow`, `currentTimeMsProvider`, `onStartTelemetrySession`, `onStopTelemetrySession`
- Instancia `GameController`
- Renderiza `KorgeGameView` e painel de debug
- Contém `gameSessionConfig` (SessionConfig)

### SessionSignalCard.kt
- Card mostrando sinais da sessão (session status, speed, distance, velocities)

### TelemetryStatusCard.kt
- Card mostrando telemetria (movement metrics, escape score)

### StatusDisplay.kt
- Card de status do jogo (distance, score, risk, horde pressure, result)

### StatusRow.kt
- Componente auxiliar para exibir par label/value

## App.kt Alterações

- Remove `GameScreen`, `SessionSignalCard`, `TelemetryStatusCard`, `StatusDisplay`, `StatusRow`
- Mantém apenas composable raiz `App()` que chama `LoginScreen()`

## Dependencies

- Mantém imports existentes (GameController, KorgeGameView, TelemetryState, etc.)
- Novos componentes ficam no pacote `com.pedroaba.tccmobile.features.game.screens.components`

## Implementação

1. Criar diretório `features/game/screens/components/`
2. Criar arquivos dos componentes em `components/`
3. Criar `GameScreen.kt`
4. Atualizar `App.kt` para usar a nova estrutura
# Handoff Mobile -> Backend

## Objetivo

Este documento resume, de forma detalhada, o que o app mobile Android ja conseguiu integrar com o backend atual e o que ainda falta para concluir a integracao das telas que ja existem no app.

O foco aqui e ajudar o time de backend a entender:

- quais fluxos do app ja estao funcionando com a API atual;
- quais telas ainda estao abastecidas com dados estaticos ou parciais;
- quais endpoints ou contratos faltam para concluir cada tela;
- quais campos o mobile precisa receber para remover placeholders da UI.

## Estado atual da integracao

### Ja integrado de verdade

- `POST /auth/login`
- `POST /auth/register`
- `POST /sessions/start`
- `POST /sessions/{sessionId}/finish`
- WebSocket STOMP:
  - conexao em `/ws`
  - envio de biometria em `/app/train/data`
  - recebimento de leaderboard em `/topic/session/{sessionId}/leaderboard`
  - recebimento do estado remoto do jogo em `/topic/session/{sessionId}/game-state`

### Ja refletido no app

- Login real com persistencia de sessao local.
- Cadastro real com persistencia de sessao local.
- Abertura de sessao remota antes de iniciar a corrida local.
- Encerramento de sessao remota ao finalizar a corrida.
- Envio continuo de biometria derivada da telemetria local.
- Atualizacao de ranking em tempo real durante a sessao ativa.
- Atualizacao do estado remoto da corrida durante a sessao ativa.
- Exibicao de `name` e `email` reais da sessao autenticada.

### Ainda nao coberto pelo backend atual

- Perfil completo do usuario.
- Edicao persistente de perfil.
- Historico de sessoes.
- Ranking global, semanal, mensal ou fora de sessao ativa.
- Busca de usuarios.
- Sistema de amizades e convites.
- Dados persistidos de dispositivo/watch pareado.

---

## Tela por tela

## 1. Login

### Status

Implementado.

### O que o app usa

- `POST /auth/login`

### Request esperado

```json
{
  "email": "usuario@exemplo.com",
  "password": "senha123"
}
```

### Response esperada

```json
{
  "token": "jwt...",
  "userId": "uuid-ou-string",
  "name": "Nome do usuario",
  "email": "usuario@exemplo.com"
}
```

### Observacoes

- O app agora mostra erro real de backend na tela, nao apenas em log.
- O `token`, `userId`, `name` e `email` sao persistidos localmente.

### Nao ha pendencia de backend para esta tela

---

## 2. Cadastro

### Status

Implementado.

### O que o app usa

- `POST /auth/register`

### Request esperada pelo mobile

```json
{
  "email": "usuario@exemplo.com",
  "name": "Nome do usuario",
  "password": "senha123",
  "birthdayDate": "2000-05-15",
  "height": 1.8,
  "weight": 75.5
}
```

### Observacoes

- A tela do app coleta a data visualmente e agora a converte para formato ISO `yyyy-MM-dd`.
- O campo `maxHeartRate` existe visualmente na tela, mas hoje nao e enviado porque o contrato atual do backend nao o aceita no cadastro.

### Pendencia de backend opcional

Se o backend quiser suportar o campo de FC maxima diretamente no cadastro, seria necessario aceitar:

```json
{
  "maxHeartRate": 190
}
```

junto dos demais campos.

Hoje isso nao bloqueia a tela, mas impede completar o cadastro fisiologico dentro da mesma chamada.

---

## 3. Home

### Status

Parcialmente integrada.

### O que ja esta real

- O botao de iniciar sessao de corrida dispara o fluxo real de abertura de sessao no backend.
- O nome exibido pode usar o `name` da sessao autenticada.

### O que ainda esta estatico

- Cards de ultima sessao.
- Resumo de metricas acumuladas.
- Ranking semanal exibido na home.
- Estado mais rico de watch/dispositivo pareado.

### Endpoints faltantes para fechar a tela

#### 3.1 Resumo da home do usuario

Sugestao:

- `GET /me/home-summary`

### Payload minimo esperado

```json
{
  "userName": "Pedro",
  "totalSessions": 12,
  "totalHordes": 37,
  "lastSession": {
    "title": "Distrito Industrial",
    "distanceKm": 6.4,
    "durationMinutes": 42,
    "status": "SURVIVED"
  },
  "weeklyRanking": {
    "position": 18,
    "score": 1284
  },
  "watchStatus": {
    "connected": false,
    "deviceName": null
  }
}
```

### Sem esse endpoint

A home continua abrindo e iniciando corrida, mas o restante dos cards fica visualmente pronto e sem dados reais.

---

## 4. Game / Corrida

### Status

Implementado no escopo suportado hoje.

### O que o app usa

- `POST /sessions/start`
- `POST /sessions/{sessionId}/finish`
- `/ws`
- `/app/train/data`
- `/topic/session/{sessionId}/leaderboard`
- `/topic/session/{sessionId}/game-state`

### Fluxo atual no app

1. Usuario inicia corrida.
2. App abre sessao remota em `POST /sessions/start`.
3. Backend devolve `sessionId`.
4. App conecta no WebSocket.
5. App assina `/topic/session/{sessionId}/leaderboard`.
6. App assina `/topic/session/{sessionId}/game-state`.
7. App envia biometria em `/app/train/data`.
8. Ao encerrar corrida, app chama `POST /sessions/{sessionId}/finish`.

### Payload de biometria que o app envia

```json
{
  "sessionId": "session-123",
  "userId": "user-456",
  "timestamp": 1710000000000,
  "bpm": 148,
  "cadence": 90.0,
  "speed": 12.24,
  "pace": 4.9,
  "accumulatedDistance": 0.42,
  "accumulatedCalories": 63.0
}
```

### Observacoes importantes

- O app envia esse payload em throttle fixo, hoje pensado para nao spammar o backend.
- `bpm` cai para `0` quando nao houver biofeedback disponivel.
- A sessao atual e aberta como treino livre, com `hordeId = null`.

### Pendencias de backend que melhorariam a tela

#### 4.1 Recuperacao do leaderboard via REST

Hoje o app depende de WebSocket ativo para recuperar o ranking da sessao.

Endpoint sugerido:

- `GET /sessions/{sessionId}/leaderboard`

### Payload esperado

Mesmo formato do `LeaderboardResponse`:

```json
{
  "sessionId": "session-123",
  "userRank": 2,
  "hordeVirtualDistanceKm": 0.75,
  "entries": [
    {
      "userId": "u1",
      "rank": 1,
      "distanceKm": 1.2
    }
  ]
}
```

### Beneficio

- recuperar estado apos reconexao;
- abrir tela de ranking da sessao mesmo depois de perda temporaria do socket;
- melhorar consistencia do app em falhas de rede.

#### 4.2 Persistencia e consulta do resultado final da sessao

Hoje o encerramento existe, mas o app nao consegue depois listar ou consultar esse resultado de forma estruturada.

Endpoint sugerido:

- `GET /sessions/{sessionId}`

### Payload minimo esperado

```json
{
  "sessionId": "session-123",
  "trainType": "RUN",
  "status": "FINISHED",
  "startDate": "2026-05-09T10:00:00Z",
  "endDate": "2026-05-09T10:42:00Z",
  "totalDistanceKm": 6.4,
  "estimatedCalories": 420.0,
  "result": "SURVIVED"
}
```

---

## 5. Ranking

### Status

Parcialmente integrada.

### O que ja esta real

- Durante uma sessao ativa, a tela consegue exibir:
  - posicao do usuario;
  - leaderboard recebido em tempo real;
  - distancia virtual da horda, quando vier do backend.

### O que ainda falta

- ranking global;
- ranking semanal;
- ranking mensal;
- ranking fora de uma sessao ativa;
- nomes publicos dos usuarios no leaderboard.

### Problema atual

No contrato atual do WebSocket, cada entrada do leaderboard tem:

```json
{
  "userId": "u1",
  "rank": 1,
  "distanceKm": 1.2
}
```

Isso obriga o app a mostrar `userId` em vez de nome amigavel.

### Endpoints e ajustes faltantes

#### 5.1 Ranking global / semanal / mensal

Sugestoes:

- `GET /ranking?period=weekly`
- `GET /ranking?period=monthly`
- `GET /ranking?period=global`

### Payload minimo esperado

```json
{
  "period": "weekly",
  "currentUser": {
    "userId": "u2",
    "name": "Pedro Barbosa",
    "position": 18,
    "score": 1284
  },
  "entries": [
    {
      "userId": "u1",
      "name": "Ravi",
      "position": 1,
      "score": 2100
    }
  ]
}
```

#### 5.2 Nome amigavel no leaderboard da sessao

Idealmente o payload em tempo real deveria evoluir para incluir `name`.

Hoje:

```json
{
  "userId": "u1",
  "rank": 1,
  "distanceKm": 1.2
}
```

Sugerido:

```json
{
  "userId": "u1",
  "name": "Ravi",
  "rank": 1,
  "distanceKm": 1.2
}
```

### Beneficio

- elimina exibicao de identificadores tecnicos na UI;
- permite tela de ranking realmente pronta para uso.

---

## 6. Profile

### Status

Parcialmente integrada.

### O que ja esta real

- `name` vindo da sessao autenticada.
- `email` vindo da sessao autenticada.

### O que ainda esta mockado/estatico

- estatisticas acumuladas do usuario;
- melhor sessao;
- aproveitamento;
- dispositivo pareado;
- ultimas hordas;
- status persistido de smartwatch/sensores;
- compartilhamento de ID social util.

### Endpoint faltante principal

- `GET /me`

### Payload minimo esperado

```json
{
  "userId": "uuid",
  "name": "Pedro Barbosa",
  "email": "pedro@email.com",
  "birthdayDate": "2000-05-15",
  "height": 1.8,
  "weight": 75.5,
  "maxHeartRate": 190,
  "stats": {
    "totalDistanceKm": 184.0,
    "bestSessionMinutes": 42,
    "successRate": 0.92,
    "totalHordes": 37
  },
  "watch": {
    "connected": false,
    "deviceName": "Galaxy Watch"
  }
}
```

### Endpoint faltante para ultimas sessoes no perfil

- `GET /me/recent-sessions`

### Payload minimo esperado

```json
[
  {
    "sessionId": "s1",
    "title": "Distrito Industrial",
    "distanceKm": 6.4,
    "durationMinutes": 42,
    "result": "SURVIVED",
    "finishedAt": "2026-05-08T22:00:00Z"
  }
]
```

### Endpoint faltante para editar perfil

- `PUT /me`

### Request minima esperada

```json
{
  "name": "Pedro Barbosa",
  "birthdayDate": "2000-05-15",
  "height": 1.8,
  "weight": 75.5,
  "maxHeartRate": 190
}
```

---

## 7. Edit Profile

### Status

Tela pronta visualmente, sem persistencia real.

### O que falta

- endpoint para salvar alteracoes;
- retorno atualizado do usuario apos salvar;
- validacoes de dominio vindas do backend.

### Endpoint sugerido

- `PUT /me`

### Opcionalmente

- `PATCH /me`

se o backend preferir atualizacao parcial.

### Response ideal

Retornar o usuario atualizado:

```json
{
  "userId": "uuid",
  "name": "Pedro Barbosa",
  "email": "pedro@email.com",
  "birthdayDate": "2000-05-15",
  "height": 1.8,
  "weight": 75.5,
  "maxHeartRate": 190
}
```

---

## 8. History

### Status

Nao integrada.

### Motivo

O backend atual ainda nao publica endpoint de historico de sessoes do usuario.

### O que a tela precisa

- lista de sessoes do usuario;
- filtros por periodo;
- dados resumidos para cards e metricas;
- eventualmente detalhes de cada sessao.

### Endpoint minimo necessario

- `GET /sessions?userId={id}`

ou, de forma mais adequada para o app autenticado:

- `GET /me/sessions`

### Query params desejados

- `period=today`
- `period=month`
- `result=survived`
- `page=0`
- `size=20`

### Payload minimo esperado

```json
{
  "summary": {
    "totalSessions": 37,
    "survivalRate": 0.79,
    "bestDistanceKm": 6.4
  },
  "entries": [
    {
      "sessionId": "s1",
      "title": "Distrito Industrial",
      "distanceKm": 6.4,
      "durationMinutes": 42,
      "result": "SURVIVED",
      "mode": "SPRINT_FINAL",
      "finishedAt": "2026-05-08T22:00:00Z"
    }
  ]
}
```

### Sem esse endpoint

A tela continua apenas como referencia visual.

---

## 9. Social / Friends List

### Status

Nao integrada.

### Motivo

Nao ha endpoints publicados para:

- buscar usuarios;
- enviar convite;
- aceitar/recusar convite;
- listar amigos;
- listar convites pendentes.

### Endpoints necessarios

#### 9.1 Buscar usuarios

- `GET /users/search?q=...`

### Payload esperado

```json
[
  {
    "userId": "u1",
    "name": "Ravi Ferraz",
    "email": "ravi@email.com",
    "status": "NONE"
  }
]
```

#### 9.2 Listar amigos do usuario autenticado

- `GET /me/friends`

### Payload esperado

```json
[
  {
    "userId": "u1",
    "name": "Ravi Ferraz",
    "status": "ACCEPTED",
    "online": true
  }
]
```

#### 9.3 Enviar convite

- `POST /me/friends/requests`

### Request esperada

```json
{
  "recipientUserId": "u1"
}
```

#### 9.4 Listar convites recebidos/enviados

- `GET /me/friends/requests`

### Payload esperado

```json
{
  "received": [
    {
      "requestId": "r1",
      "userId": "u2",
      "name": "Lia Storm",
      "status": "PENDING"
    }
  ],
  "sent": [
    {
      "requestId": "r2",
      "userId": "u3",
      "name": "Caio Norte",
      "status": "PENDING"
    }
  ]
}
```

#### 9.5 Aceitar ou recusar convite

- `POST /me/friends/requests/{requestId}/accept`
- `POST /me/friends/requests/{requestId}/reject`

### Sem esses endpoints

As telas `Sua rede de fuga` e `Adicionar por tag` nao conseguem sair do estado ilustrativo.

---

## 10. Add Friends

### Status

Tela pronta visualmente, sem backend funcional.

### Depende diretamente de

- busca de usuarios;
- envio de convite;
- tratamento de estados como:
  - sem relacao;
  - convite enviado;
  - convite recebido;
  - amizade aceita.

### Endpoints necessarios

Os mesmos do bloco Social:

- `GET /users/search`
- `POST /me/friends/requests`
- `GET /me/friends/requests`

---

## 11. Watch / conectividade

### Status

Ainda sem integracao funcional completa nessas telas.

### Observacao

O backend atual ja recebe biometria em tempo real pelo WebSocket, mas as telas de watch do mobile ainda nao estao apoiadas por um backend especifico para:

- status do dispositivo pareado;
- historico de pareamento;
- diagnostico de conectividade;
- identificacao persistida do watch conectado.

### Endpoint opcional, se o time quiser suportar isso

- `GET /me/watch-status`

### Payload sugerido

```json
{
  "connected": false,
  "deviceName": "Galaxy Watch 6",
  "lastSeenAt": "2026-05-09T10:00:00Z",
  "batteryLevel": 0.82
}
```

---

## Prioridade sugerida para o backend

## Prioridade 1 - fecha os fluxos mais visiveis

- `GET /me`
- `PUT /me`
- `GET /me/sessions` ou `GET /sessions?userId=...`
- `GET /ranking?period=weekly|monthly|global`
- incluir `name` no payload de leaderboard em tempo real

## Prioridade 2 - fecha a parte social

- `GET /users/search`
- `GET /me/friends`
- `GET /me/friends/requests`
- `POST /me/friends/requests`
- `POST /me/friends/requests/{id}/accept`
- `POST /me/friends/requests/{id}/reject`

## Prioridade 3 - melhora resiliencia da corrida

- `GET /sessions/{sessionId}`
- `GET /sessions/{sessionId}/leaderboard`

---

## Resumo executivo

Hoje o app ja funciona de forma real em:

- autenticacao;
- abertura/encerramento de sessao de corrida;
- envio de biometria;
- ranking em tempo real durante a sessao ativa.

O que impede concluir as demais telas nao e falta de UI no mobile, e sim ausencia de endpoints de leitura e edicao para:

- perfil;
- historico;
- ranking agregado;
- amizades;
- busca de usuarios;
- estado persistente de watch/dispositivo.

Se o backend entregar primeiro `GET /me`, `PUT /me`, `GET /me/sessions` e `GET /ranking`, ja da para remover grande parte dos placeholders mais visiveis do app.

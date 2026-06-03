# Design: Fidelidade Visual das Telas e Navegação com Gate de Autenticação

## Contexto

O app já possui uma base de telas em `commonMain` para `home`, `ranking`, `profile`, `history`, `social` e `watch`, além de `login` e `signup`. As telas de autenticação já seguem a linguagem visual desejada, mas as demais ainda estão distantes do layout definido no arquivo `.pen` de referência:

- `/Users/pedroaugustobarbosaaparecido/www/tcc-mobile-deprecated/tcc-mobile-design.pen`

O objetivo desta mudança é aproximar a interface do app ao design do `.pen`, preservando o escopo como uma entrega de design e navegação. Não entram nesta etapa integração real com gameplay, telemetria, KorGE ou dados vindos de API.

Além da fidelidade visual, o app precisa deixar de abrir diretamente em `LoginScreen()` e passar a respeitar um gate de autenticação baseado no JWT já persistido.

## Objetivos

- Introduzir uma tela de loading inicial que verifica a sessão persistida.
- Redirecionar para `LoginScreen` quando não houver JWT válido.
- Redirecionar para a navegação principal quando houver JWT válido.
- Reestruturar as telas principais para ficarem visivelmente fiéis ao `.pen`.
- Preservar a linguagem visual atual de `LoginScreen` e `SignupScreen`.
- Excluir do escopo a replicação exata do gradiente presente no topo das telas do design.

## Fora de Escopo

- Integração real de métricas de gameplay nas telas.
- Alterações no pipeline de telemetria.
- Alterações na lógica do jogo e na integração KorGE.
- Refatoração profunda da camada de autenticação.
- Feature parity iOS para autenticação nativa, caso a dependência atual permaneça centrada em Android.

## Estado Atual Relevante

### Entrada do App

`App.kt` atualmente renderiza `LoginScreen()` diretamente dentro de `TccMobileTheme`, sem gate de autenticação e sem navegação principal no fluxo de abertura.

### Autenticação

No Android já existem:

- `SessionManager`, que lê e grava sessão via `DataStore`
- `AuthManager`, que expõe:
  - `AuthState.Loading`
  - `AuthState.Unauthenticated`
  - `AuthState.Authenticated`

Isso significa que o app já tem uma fonte de verdade adequada para decidir entre loading, login e home.

### Navegação e Telas

Já existe `AppNavigation.kt` com tabs e rotas internas, mas:

- não está ligado ao entrypoint atual do app;
- a composição visual das telas ainda está simplificada em relação ao `.pen`;
- vários blocos visuais são implementados diretamente nas próprias screens, o que dificulta consistência.

## Abordagem Escolhida

A implementação seguirá uma abordagem de camada compartilhada de componentes visuais, com remontagem das telas sobre essa base.

Essa abordagem foi escolhida porque:

- reduz repetição entre telas parecidas;
- melhora consistência visual;
- permite aproximar várias telas do `.pen` sem transformar a navegação em um protótipo descartável;
- mantém o escopo focado em UI e navegação, sem invadir domínio, telemetria ou gameplay.

## Fluxo de Entrada

O app passará a obedecer ao fluxo abaixo:

`Loading -> Login`
quando não houver JWT persistido válido

`Loading -> Home`
quando houver JWT persistido válido

### Regras

- `Loading`
  - tela dedicada de carregamento da sessão;
  - mesma linguagem visual escura/vermelha do app;
  - sem dependência de dados remotos adicionais.

- `Unauthenticated`
  - renderiza `LoginScreen`;
  - `SignupScreen` continua acessível pelo fluxo atual de autenticação.

- `Authenticated`
  - entra na navegação principal iniciando pela `Home`.

## Arquitetura de Navegação

### Root App

`App.kt` deixa de decidir apenas por uma screen fixa e passa a atuar como shell raiz do tema + roteamento por estado de autenticação.

### Fonte do Estado de Autenticação

Como `AuthManager` está em `androidMain` e `App.kt` está em `commonMain`, a solução deve manter a separação multiplataforma:

- o estado de autenticação será resolvido no ponto de entrada Android;
- o Compose compartilhado receberá o estado já pronto como parâmetro ou por um shell específico de plataforma;
- `commonMain` não deve importar classes Android diretamente.

Se for necessário, a estrutura pode ficar dividida em:

- um composable compartilhado que recebe um estado simplificado de autenticação;
- um entrypoint Android responsável por observar `AuthManager` e injetar esse estado.

## Estrutura Visual Compartilhada

Para evitar que cada tela replique sua própria interpretação do layout, serão criados ou reorganizados componentes compartilhados em `ui/components`.

### Componentes Base

- `AppScreenScaffold`
  - container padrão de tela com fundo, paddings, scroll e área segura para a navegação inferior.

- `TopIdentityHeader`
  - cabeçalho com avatar, nome, subtítulo e ação opcional.

- `StatusPill`
  - chip para estados como `HORDA PRONTA`, `SMARTWATCH OFFLINE`, `SOBREVIVENTE`, `RANK #18`.

- `SectionPillTabs`
  - seletor visual de segmentos curtos como `Hoje`, `Última semana`, `Sobreviveu`.

- `MetricCard`
  - card pequeno para um número principal e um rótulo.

- `MetricStrip`
  - agrupamento horizontal de métricas pequenas.

- `FeatureCard`
  - card maior com título, descrição e CTAs.

- `ListPanel`
  - container de listas com título e ação opcional.

- `ListRow`
  - item visual padronizado para ranking, histórico, hordas, sugestões e convites.

- `BottomSurvivalNav`
  - barra inferior com estados ativo/inativo e visual alinhado ao `.pen`.

## Telas no Escopo

As seguintes telas serão ajustadas visualmente:

- `HomeScreen`
- `HomeScreenWithModal`
- `RankingScreen`
- `ProfileScreen`
- `EditProfileScreen`
- `WatchDisconnectedScreen`
- `WatchConnectionStatesScreen`
- `HistoryScreen`
- `FriendsListScreen`
- `AddFriendsScreen`

## Estratégia por Tela

### Home

Objetivo:

- aproximar hierarquia visual da home principal do `.pen`;
- destacar saudação, CTA principal, estatísticas rápidas, última sessão e ranking resumido;
- preservar comportamento de abrir modal/estado de smartwatch quando aplicável.

### Ranking

Objetivo:

- trocar o hero genérico por uma estrutura próxima ao card de posição atual, pódio e ranking geral;
- representar visualmente o “top 3” e a posição do usuário sem dados reais.

### Profile

Objetivo:

- mostrar identidade do sobrevivente, métricas compactas, status do smartwatch, ações principais e últimas hordas;
- manter acesso a edição de perfil.

### Edit Profile

Objetivo:

- alinhar campos e hierarquia ao `.pen`;
- preservar o formulário como composição visual, sem ampliar regra de negócio.

### Watch Screens

Objetivo:

- representar claramente estados de conexão, sincronização, biometria e erros;
- usar barras, chips e callouts compatíveis com a referência.

### History

Objetivo:

- aproximar resumo mensal, filtros rápidos e lista de sessões do design de referência;
- preservar a navegação para estados de smartwatch, se ainda fizer sentido no fluxo já criado.

### Friends / Add Friends

Objetivo:

- alinhar busca, sugestões, convites e estados de lista ao visual do `.pen`;
- preservar as rotas de ida e volta entre telas sociais.

## Dados e Conteúdo

Nesta etapa, as telas trabalharão com conteúdo mockado/local para compor a referência visual.

Princípios:

- não acoplar design estático a sensores ou pipeline de telemetria;
- não inventar dependência com `GameController`;
- usar textos, números e estados fake apenas para compor o layout;
- manter os dados localizados dentro da screen ou em pequenos modelos auxiliares de UI, quando isso reduzir duplicação.

## Decisões de Fidelidade

- O layout deve buscar fidelidade estrutural e visual ao `.pen`.
- O gradiente superior não precisa ser replicado com exatidão.
- A prioridade é:
  - hierarquia
  - espaçamento
  - densidade visual
  - forma dos cards
  - barra inferior
  - CTAs
  - agrupamento dos blocos

## Erros e Fallbacks

### Sessão não encontrada

- loading encerra normalmente;
- usuário é encaminhado para `LoginScreen`.

### Dependência Android de autenticação

Se a autenticação não puder ser observada diretamente de `commonMain`, o roteamento inicial permanecerá com um adapter de plataforma no Android.

### Navegação interna

Se alguma tela interna depender de parâmetros que ainda não existem, usar valores mockados temporários no próprio fluxo de UI em vez de expandir a arquitetura.

## Testes e Verificação

### Verificação funcional

- build do módulo Android compartilhado;
- fluxo sem JWT:
  - abre loading;
  - segue para login.
- fluxo com JWT válido persistido:
  - abre loading;
  - segue para home.
- navegação entre tabs principais:
  - `Home`
  - `Ranking`
  - `Perfil`
  - `Social`
- navegação para subrotas:
  - `Edit Profile`
  - `History`
  - `Watch Connection`
  - `Watch Disconnected`
  - `Add Friends`

### Verificação visual

- conferir consistência de paddings e espaçamento;
- conferir destaque do item ativo na barra inferior;
- conferir uniformidade entre cards pequenos e grandes;
- conferir que nenhuma das telas no escopo está usando composição antiga visivelmente destoante do `.pen`.

## Arquivos Esperados no Escopo de Mudança

### Prováveis alterações diretas

- `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/App.kt`
- `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/ui/navigation/AppNavigation.kt`
- `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/home/screens/*`
- `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/ranking/screens/*`
- `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/profile/screens/*`
- `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/watch/screens/*`
- `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/history/screens/*`
- `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/social/screens/*`
- componentes novos ou ajustados em `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/ui/components/*`

### Possível apoio no Android

- `composeApp/src/androidMain/kotlin/com/pedroaba/tccmobile/MainActivity.kt`
  - apenas se necessário para injetar `AuthState` no Compose compartilhado.

## Plano de Implementação de Alto Nível

1. Conectar o entrypoint do app ao estado de autenticação existente.
2. Criar a tela de loading e o gate `loading/login/home`.
3. Consolidar a navegação principal e subrotas.
4. Extrair componentes visuais compartilhados.
5. Reescrever as telas do escopo com base nesses componentes.
6. Validar build e navegação.

## Resultado Esperado

Ao final desta etapa, o app deve:

- abrir respeitando a sessão persistida;
- exibir uma navegação coerente entre login e área autenticada;
- apresentar as telas principais com aparência próxima ao design do `.pen`;
- manter a implementação restrita a UI e navegação, sem quebrar o eixo atual de telemetria e jogo.

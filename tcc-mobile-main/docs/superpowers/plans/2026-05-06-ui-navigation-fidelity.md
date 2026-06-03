# UI Navigation Fidelity Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement the loading/login/home authentication gate and rebuild the authenticated app shell screens so they visually track the approved `.pen` design while staying mock-data only.

**Architecture:** Keep Android as the source of truth for persisted auth via `AuthManager` in `MainActivity`, and use the shared Compose layer for the authenticated shell and screen composition. Introduce a small set of reusable UI shell components in `commonMain`, then rebuild each authenticated screen on top of those components instead of keeping per-screen one-off layouts.

**Tech Stack:** Kotlin Multiplatform, Compose Multiplatform, Material 3, Android `DataStore`, Android `AuthManager`, shared UI components in `commonMain`

---

### Task 1: Lock Down the Auth Entry Flow

**Files:**
- Modify: `composeApp/src/androidMain/kotlin/com/pedroaba/tccmobile/MainActivity.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/App.kt`
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/auth/screens/AuthLoadingScreen.kt`

- [ ] **Step 1: Confirm the current auth gate entrypoints**

Run:

```bash
sed -n '1,260p' composeApp/src/androidMain/kotlin/com/pedroaba/tccmobile/MainActivity.kt
sed -n '1,120p' composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/App.kt
```

Expected: `MainActivity` already branches on `AuthState`, while `App.kt` still renders a fixed `LoginScreen()`.

- [ ] **Step 2: Add a dedicated shared loading screen**

Create `AuthLoadingScreen.kt` with a centered loading UI using the same dark/red visual language as the auth screens, for example:

```kotlin
@Composable
fun AuthLoadingScreen() {
    AppRootContainer {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppBadge("SINCRONIZANDO SESSAO")
            Spacer(modifier = Modifier.height(AppTheme.spacing.lg))
            AppSpinner(modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(AppTheme.spacing.md))
            AppHero("Carregando sobrevivente.")
            AppBody("Verificando JWT salvo neste dispositivo.")
        }
    }
}
```

- [ ] **Step 3: Make the common root app reusable instead of hardcoded**

Update `App.kt` so the root composable wraps theme only and renders a passed content lambda or a small shared router component, for example:

```kotlin
@Composable
fun App(content: @Composable () -> Unit) {
    TccMobileTheme {
        content()
    }
}
```

- [ ] **Step 4: Route `Loading`, `Unauthenticated`, and `Authenticated` through the shared shell**

Update `MainActivity.kt` so:

- `AuthState.Loading` renders `AuthLoadingScreen()`
- `AuthState.Unauthenticated` renders the existing auth flow
- `AuthState.Authenticated` renders the authenticated navigation shell

Expected code shape:

```kotlin
setContent {
    App {
        when (val state = authState) {
            AuthState.Loading -> AuthLoadingScreen()
            AuthState.Unauthenticated -> AuthScreenContent(...)
            is AuthState.Authenticated -> MainAppNavigation(...)
        }
    }
}
```

- [ ] **Step 5: Compile Android shared code**

Run: `./gradlew :composeApp:compileDebugKotlinAndroid`

Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add composeApp/src/androidMain/kotlin/com/pedroaba/tccmobile/MainActivity.kt composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/App.kt composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/auth/screens/AuthLoadingScreen.kt
git commit -m "feat: add auth loading gate"
```

### Task 2: Build the Shared Authenticated Shell Components

**Files:**
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/ui/components/AppScreenScaffold.kt`
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/ui/components/TopIdentityHeader.kt`
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/ui/components/StatusPill.kt`
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/ui/components/SectionPillTabs.kt`
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/ui/components/MetricCard.kt`
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/ui/components/FeatureCard.kt`
- Create: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/ui/components/ListPanel.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/ui/components/navigation/FloatingTabBar.kt`

- [ ] **Step 1: Read the existing shared component style**

Run:

```bash
sed -n '1,240p' composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/ui/components/AppCard.kt
sed -n '1,240p' composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/ui/components/AppButton.kt
sed -n '1,240p' composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/ui/components/navigation/FloatingTabBar.kt
```

Expected: enough context to keep colors, spacing, and typography consistent with the existing theme.

- [ ] **Step 2: Add a shared screen scaffold with bottom-nav spacing**

Create `AppScreenScaffold.kt` with a scrollable content slot and optional fixed bottom bar padding:

```kotlin
@Composable
fun AppScreenScaffold(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) { ... }
```

- [ ] **Step 3: Add reusable shell building blocks**

Create focused components for:

- compact identity header
- pills/chips
- segmented pill tabs
- metric cards/rows
- list panels/rows
- large CTA card sections

Use hardcoded mock labels in screen files, not inside these generic components.

- [ ] **Step 4: Restyle the bottom tab bar to match the `.pen` shell**

Update `FloatingTabBar.kt` so the bar uses:

- stronger rounded container
- darker inactive state
- red active pill state
- labels aligned with the approved `home/rank/perfil/social` map

- [ ] **Step 5: Compile Android shared code**

Run: `./gradlew :composeApp:compileDebugKotlinAndroid`

Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/ui/components composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/ui/components/navigation/FloatingTabBar.kt
git commit -m "feat: add authenticated app shell components"
```

### Task 3: Rebuild Home and Ranking Against the Design

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/home/screens/HomeScreen.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/home/screens/HomeScreenWithModal.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/ranking/screens/RankingScreen.kt`

- [ ] **Step 1: Re-read the target screens and current implementations**

Run:

```bash
sed -n '1,260p' composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/home/screens/HomeScreen.kt
sed -n '1,260p' composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/home/screens/HomeScreenWithModal.kt
sed -n '1,260p' composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/ranking/screens/RankingScreen.kt
```

- [ ] **Step 2: Recompose `HomeScreen` using the new shell**

Implement:

- compact greeting row
- primary “inicie uma nova sessao/fuga” card
- twin metric cards
- latest session panel
- ranking preview panel

Keep `onStartRun`, `onViewProfile`, and `onShowWatchModal` behavior wired.

- [ ] **Step 3: Recompose `HomeScreenWithModal`**

Implement the disconnected smartwatch modal in the same layout language as the `.pen`, preserving dismiss and CTA wiring.

- [ ] **Step 4: Recompose `RankingScreen`**

Implement:

- heading copy close to the design
- current rank summary card
- top-3 podium card
- general leaderboard list

- [ ] **Step 5: Compile Android shared code**

Run: `./gradlew :composeApp:compileDebugKotlinAndroid`

Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/home/screens composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/ranking/screens
git commit -m "feat: restyle home and ranking screens"
```

### Task 4: Rebuild Profile, Edit Profile, and Watch States

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/profile/screens/ProfileScreen.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/profile/screens/EditProfileScreen.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/watch/screens/WatchConnectionStatesScreen.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/watch/screens/WatchDisconnectedScreen.kt`

- [ ] **Step 1: Re-read the current implementations**

Run:

```bash
sed -n '1,260p' composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/profile/screens/ProfileScreen.kt
sed -n '1,260p' composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/profile/screens/EditProfileScreen.kt
sed -n '1,260p' composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/watch/screens/WatchConnectionStatesScreen.kt
sed -n '1,260p' composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/watch/screens/WatchDisconnectedScreen.kt
```

- [ ] **Step 2: Recompose `ProfileScreen`**

Implement:

- survivor identity card
- metric strip
- smartwatch status panel
- action row
- latest horde list panel

- [ ] **Step 3: Recompose `EditProfileScreen`**

Implement the reference-style edit form with stacked fields and action emphasis, while keeping existing callbacks and local-only behavior.

- [ ] **Step 4: Recompose the watch state screens**

Implement:

- connection checklist/status panels
- progress indicators where needed
- error state callout and retry CTA for disconnected flow

- [ ] **Step 5: Compile Android shared code**

Run: `./gradlew :composeApp:compileDebugKotlinAndroid`

Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/profile/screens composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/watch/screens
git commit -m "feat: restyle profile and watch screens"
```

### Task 5: Rebuild Social and History Flows

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/history/screens/HistoryScreen.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/social/screens/FriendsListScreen.kt`
- Modify: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/social/screens/AddFriendsScreen.kt`

- [ ] **Step 1: Re-read the current implementations**

Run:

```bash
sed -n '1,260p' composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/history/screens/HistoryScreen.kt
sed -n '1,260p' composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/social/screens/FriendsListScreen.kt
sed -n '1,260p' composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/social/screens/AddFriendsScreen.kt
```

- [ ] **Step 2: Recompose `HistoryScreen`**

Implement:

- title and filter pills
- summary metrics
- monthly summary card
- latest sessions list

- [ ] **Step 3: Recompose `FriendsListScreen` and `AddFriendsScreen`**

Implement:

- friends summary and invite CTA
- suggestion rows
- add-friends search/input panel
- pending invites panel

- [ ] **Step 4: Verify route wiring still matches `AppNavigation` / `MainActivity`**

Check that callbacks still route to:

- `history`
- `watch_connection`
- `watch_disconnected`
- `add_friends`

- [ ] **Step 5: Compile Android shared code**

Run: `./gradlew :composeApp:compileDebugKotlinAndroid`

Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/history/screens composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/social/screens
git commit -m "feat: restyle history and social screens"
```

### Task 6: Consolidate Navigation and Verify Behavior

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/ui/navigation/AppNavigation.kt`
- Modify: `composeApp/src/androidMain/kotlin/com/pedroaba/tccmobile/MainActivity.kt`

- [ ] **Step 1: Decide the single source of truth for authenticated navigation**

Either:

- move the authenticated router into shared `AppNavigation.kt`, or
- keep it in `MainActivity.kt` and delete/retire the duplicate shared router

Choose one; do not leave two competing navigation implementations active.

- [ ] **Step 2: Normalize route names and screen wiring**

Ensure the selected router consistently handles:

```kotlin
"home", "rank", "perfil", "social", "edit_profile", "add_friends", "history", "watch_connection", "watch_disconnected", "game"
```

- [ ] **Step 3: Compile Android shared code**

Run: `./gradlew :composeApp:compileDebugKotlinAndroid`

Expected: PASS

- [ ] **Step 4: Build the Android app**

Run: `./gradlew :composeApp:assembleDebug`

Expected: PASS

- [ ] **Step 5: Sanity-check navigation behavior manually**

Verify:

- no JWT -> loading -> login
- saved JWT -> loading -> home
- bottom tabs switch correctly
- profile -> edit profile works
- social -> add friends works
- history -> watch connection -> disconnected works

- [ ] **Step 6: Commit**

```bash
git add composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/ui/navigation/AppNavigation.kt composeApp/src/androidMain/kotlin/com/pedroaba/tccmobile/MainActivity.kt
git commit -m "feat: consolidate authenticated navigation shell"
```

### Task 7: Final Verification and Cleanup

**Files:**
- Review: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/features/**`
- Review: `composeApp/src/commonMain/kotlin/com/pedroaba/tccmobile/ui/components/**`
- Review: `composeApp/src/androidMain/kotlin/com/pedroaba/tccmobile/MainActivity.kt`

- [ ] **Step 1: Check git diff for accidental scope creep**

Run:

```bash
git status --short
git diff --stat
```

Expected: only UI/navigation/auth-entry files are touched intentionally.

- [ ] **Step 2: Run the final targeted compile/build**

Run:

```bash
./gradlew :composeApp:compileDebugKotlinAndroid
./gradlew :composeApp:assembleDebug
```

Expected: PASS

- [ ] **Step 3: Summarize residual risks**

Document any remaining gaps such as:

- iOS auth parity not implemented
- game screen still separate from the new authenticated shell visual system
- mock data still backing the redesigned cards

- [ ] **Step 4: Commit**

```bash
git add .
git commit -m "chore: finalize UI navigation fidelity update"
```

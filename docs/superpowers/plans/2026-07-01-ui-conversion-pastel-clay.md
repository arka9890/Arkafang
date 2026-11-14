# UI Conversion Pastel Clay Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Konversi seluruh UI openfang-android (4 layar: Dashboard, Chat, Logs, Setup) dari Material3 default ke design system Pastel Clay dengan dukungan dark mode, sambil mempertahankan semua logika ViewModel, Hilt, Proot, dan OpenFangApiClient yang sudah ada.

**Architecture:** Lapisan UI Compose diganti dengan `PastelClayTheme` custom (light + dark) + komponen claymorphism (card, button, toggle, chip, input, floating nav dock, sidebar drawer, model selector). ViewModel dan Hilt DI tetap dihubungkan via `hiltViewModel()` — tidak ada perubahan pada logika bisnis.

**Tech Stack:** Kotlin 2.0.20, Jetpack Compose 100%, Hilt 2.51, Material3 (dasar untuk baseline), Plus Jakarta Sans font, Material Symbols Outlined icons.

**Spec:** `docs/openfang-android/ui-conversion-pastel-clay-design.md`

## Global Constraints

- Bahasa: Kotlin 100%, Jetpack Compose 100%
- Min SDK: 26 (Android 8.0 Oreo)
- Build: Gradle 8.13, AGP 8.5.2, Kotlin DSL (`.kts`)
- Font: Plus Jakarta Sans (regular, bold, semibold, medium) — TTF files harus disediakan di `res/font/`
- Icon: Material Symbols Outlined (sudah tersedia via Material3)
- Warna: Palet Pastel Clay terang dan gelap (lihat Tabel 3.1 di design doc)
- Bentuk: Pill-shaped (9999dp / full) untuk buttons, chips, toggles; rounded-3xl (24-32dp) untuk cards
- Elevation: Claymorphism custom (drop shadow + inset highlight + inset shadow) via custom Modifier
- Navigasi: Floating Navigation Dock (3 tab: Chat, Provider, Pengaturan) di bawah layar
- Hilt: `hiltViewModel()` tetap digunakan di semua screen
- Dark mode: `PastelClayTheme(isDark: Boolean)`; preferensi disimpan di `SharedPreferences`
- Tidak ada perubahan pada file Rust (`crates/`), SDK (`sdk/`), atau ViewModel esist
- Tidak ada fitur baru — hanya retampilan UI

## Review Focus

| # | Input class / failure mode | Perilaku yang diharapkan | Task owner |
|---|---------------------------|------------------------|------------|
| 1 | Dark mode toggle disimpan dan dipulihkan setelah restart app | Preferensi tema bertahan di `SharedPreferences`, diterapkan saat `MainActivity` buka | Task 9 |
| 2 | Claymorphism shadow pada berbagai densitas layar (ldpi, mdpi, hdpi, xhdpi, xxhdpi) | Shadow terlihat konsisten, tidak terlalu tebal atau tipis pada layar kecil/besar | Task 1 |
| 3 | Sidebar drawer backdrop tap | Menekap di area blur belakang drawer menutup drawer | Task 3 |
| 4 | Font Plus Jakarta Sans tidak tersedia (TTF hilang) | App tidak crash, fallback ke font sistem default (sans-serif) | Task 9 |
| 5 | Chat streaming delta render dalam clay message bubble | Pesan AI yang streaming muncul dalam bubble clay dengan warna yang benar, tidak ada glitch visual | Task 6 |

---

## File Structure

### File baru (create)

| Path | Tanggung jawab |
|------|---------------|
| `app/src/main/java/com/openfang/mobile/ui/theme/PastelClayTheme.kt` | `PastelClayTheme(isDark, content)` — ColorScheme, Typography, Shapes light + dark |
| `app/src/main/java/com/openfang/mobile/ui/theme/ClayShadow.kt` | Custom `Modifier.clayElevation()` dan `Modifier.clayPressed()` untuk drop shadow + inset highlight |
| `app/src/main/java/com/openfang/mobile/ui/theme/ClayCard.kt` | `ClayCard(content)` — frosted card dengan clay shadow dan pill radius |
| `app/src/main/java/com/openfang/mobile/ui/theme/ClayButton.kt` | `ClayButton(onClick, content)` — pill button, primary variant + pressed state |
| `app/src/main/java/com/openfang/mobile/ui/theme/ClayToggle.kt` | `ClayToggle(checked, onCheckedChange)` — pill switch dengan thumb clay |
| `app/src/main/java/com/openfang/mobile/ui/theme/ClayChip.kt` | `ClayChip(label, selected, onClick)` — pill chip untuk filter/prompt |
| `app/src/main/java/com/openfang/mobile/ui/theme/ClayInput.kt` | `ClayInput(value, onValueChange, placeholder)` — recessed input field |
| `app/src/main/java/com/openfang/mobile/ui/theme/FloatingNavDock.kt` | `FloatingNavDock(selectedTab, onTabClick)` — bottom nav dock frosted glass |
| `app/src/main/java/com/openfang/mobile/ui/theme/SidebarDrawer.kt` | `SidebarDrawer(isOpen, onClose, content)` — slide-left drawer dengan backdrop |
| `app/src/main/java/com/openfang/mobile/ui/theme/ModelSelector.kt` | `ModelSelector(selectedModel, onModelClick)` — sticky top bar model dropdown |
| `app/src/main/java/com/openfang/mobile/ui/theme/components/MessageBubbleClay.kt` | `MessageBubbleClay(msg, isUser)` — chat bubble clay untuk user/AI/system |
| `app/src/main/java/com/openfang/mobile/ui/theme/components/HandCardClay.kt` | `HandCardClay(hand, onActivate, onPause)` — dashboard hand card clay |
| `app/src/main/java/com/openfang/mobile/ui/theme/components/PromptPill.kt` | `PromptPill(label, onClick)` — horizontal prompt suggestion pill |
| `app/src/main/java/com/openfang/mobile/ui/theme/components/ReactionButtons.kt` | `ReactionButtons(onLike, onDislike, onRegenerate)` — reaksi suka/tidak suka/ulang |
| `app/src/main/java/com/openfang/mobile/ui/dashboard/DashboardScreenClay.kt` | Dashboard screen dengan desain clay |
| `app/src/main/java/com/openfang/mobile/ui/chat/ChatScreenClay.kt` | Chat screen dengan sidebar drawer + model selector + clay bubbles |
| `app/src/main/java/com/openfang/mobile/ui/logs/LogsScreenClay.kt` | Logs screen dengan desain clay |
| `app/src/main/java/com/openfang/mobile/ui/setup/SetupScreenClay.kt` | Setup screen dengan desain clay |
| `app/src/main/res/values/fonts.xml` | Font family definition untuk Plus Jakarta Sans |

### File yang diubah (modify)

| Path | Perubahan |
|------|-----------|
| `app/src/main/java/com/openfang/mobile/MainActivity.kt` | Ganti `AppRoot()` dan `NavTab()` dengan Clay screens + `FloatingNavDock`; tambahkan dark mode state |
| `app/build.gradle.kts` | (Opsional) Tidak ada dependensi baru — font via resource saja |

### File yang TIDAK diubah

- Semua `*Vm.kt` (ChatVm, DashboardVm, LogsVm, SetupVm)
- `ProotOrchestrator.kt`, `ProotService.kt`, `StatusNotifier.kt`
- `OpenFangApiClient.kt`, `Dtos.kt`, `OpenFangApi.kt`
- `TokenStore.kt`, `SetupStateStore.kt`, `MirrorRegistry.kt`, `EnvironmentDownloader.kt`, `EnvironmentUnpacker.kt`, `IntegrityVerifier.kt`
- `AppModule.kt` (Hilt DI)
- `AndroidManifest.xml`, `res/values/themes.xml`, `res/values/strings.xml`, `res/raw/mirrors.json`

---

## Task Rencana

### Task 1: Theme foundation — PastelClayTheme + ClayShadow

**Files:**
- Create: `app/src/main/java/com/openfang/mobile/ui/theme/PastelClayTheme.kt`
- Create: `app/src/main/java/com/openfang/mobile/ui/theme/ClayShadow.kt`

**Interfaces:**
- Consumes: (tidak ada — task pertama)
- Produces: `PastelClayTheme(isDark: Boolean = false, content: @Composable () -> Unit)`, `Modifier.clayElevation()`, `Modifier.clayPressed()`

- [ ] **Step 1: Write failing test for theme colors**

```kotlin
// app/src/test/java/com/openfang/mobile/ui/theme/PastelClayThemeTest.kt
@Test fun lightTheme_primaryColor_matchesSpec() {
    // Arrange + Act
    val theme = PastelClayTheme(isDark = false) { }
    // Assert: colorScheme.primary == #4361EE
}
```

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.theme.PastelClayThemeTest"`
Expected: FAIL (class not found)

- [ ] **Step 2: Implement `PastelClayTheme` di `PastelClayTheme.kt`**

Definisikan `ColorScheme` light dan dark sesuai Tabel 3.1 design doc, `Typography` dengan Plus Jakarta Sans, `Shapes` dengan pill full + rounded-3xl. Gunakan `MaterialTheme` sebagai wrapper internal atau custom `CompositionLocal`.

- [ ] **Step 3: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.theme.PastelClayThemeTest"`
Expected: PASS

- [ ] **Step 4: Write failing test for ClayShadow**

```kotlin
@Test fun clayElevation_modifier_returnsNonNull() {
    val modifier = Modifier.clayElevation()
    assertNotNull(modifier)
}
```

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.theme.ClayShadowTest"`
Expected: FAIL

- [ ] **Step 5: Implement `Modifier.clayElevation()` dan `clayPressed()` di `ClayShadow.kt`**

Gunakan `drawBehind` dengan `drawShadow` (outer diffuse) + `inset` untuk inner highlight dan inner shadow. Warna shadow: `rgba(35, 62, 130, 0.08)` outer, `rgba(255, 255, 255, 0.8)` top inset, `rgba(110, 140, 209, 0.12)` bottom inset.

- [ ] **Step 6: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.theme.ClayShadowTest"`
Expected: PASS

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/com/openfang/mobile/ui/theme/PastelClayTheme.kt app/src/main/java/com/openfang/mobile/ui/theme/ClayShadow.kt app/src/test/java/com/openfang/mobile/ui/theme/PastelClayThemeTest.kt app/src/test/java/com/openfang/mobile/ui/theme/ClayShadowTest.kt
git commit -m "feat(ui): add PastelClayTheme light/dark and ClayShadow modifier"
```

---

### Task 2: Basic clay components — Card, Button, Toggle, Chip, Input

**Files:**
- Create: `app/src/main/java/com/openfang/mobile/ui/theme/ClayCard.kt`
- Create: `app/src/main/java/com/openfang/mobile/ui/theme/ClayButton.kt`
- Create: `app/src/main/java/com/openfang/mobile/ui/theme/ClayToggle.kt`
- Create: `app/src/main/java/com/openfang/mobile/ui/theme/ClayChip.kt`
- Create: `app/src/main/java/com/openfang/mobile/ui/theme/ClayInput.kt`

**Interfaces:**
- Consumes: `PastelClayTheme`, `Modifier.clayElevation()` (Task 1)
- Produces: `ClayCard`, `ClayButton`, `ClayToggle`, `ClayChip`, `ClayInput` composables

- [ ] **Step 1: Write failing test for ClayCard**

```kotlin
@Test fun clayCard_rendersWithPillShape() {
    // Compose test: verify ClayCard has rounded shape and clay elevation
}
```

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.theme.ClayCardTest"`
Expected: FAIL

- [ ] **Step 2: Implement `ClayCard` di `ClayCard.kt`**

Surface dengan warna surface-container-low, shape rounded-3xl (32dp), padding space-lg (20dp), clayElevation modifier.

- [ ] **Step 3: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.theme.ClayCardTest"`
Expected: PASS

- [ ] **Step 4: Write failing test for ClayButton**

```kotlin
@Test fun clayButton_onClick_invokesCallback() {
    var clicked = false
    // Compose test: click ClayButton, assert clicked == true
}
```

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.theme.ClayButtonTest"`
Expected: FAIL

- [ ] **Step 5: Implement `ClayButton` di `ClayButton.kt`**

Pill-shaped (full), primary fill `#4361EE`, text white, clayElevation default, clayPressed on active, 1dp translate down on press.

- [ ] **Step 6: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.theme.ClayButtonTest"`
Expected: PASS

- [ ] **Step 7: Implement `ClayToggle`, `ClayChip`, `ClayInput`** (sama pola: test → implement → verify)

- [ ] **Step 8: Run all component tests**

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.theme.*"`
Expected: PASS

- [ ] **Step 9: Commit**

```bash
git add app/src/main/java/com/openfang/mobile/ui/theme/ClayCard.kt app/src/main/java/com/openfang/mobile/ui/theme/ClayButton.kt app/src/main/java/com/openfang/mobile/ui/theme/ClayToggle.kt app/src/main/java/com/openfang/mobile/ui/theme/ClayChip.kt app/src/main/java/com/openfang/mobile/ui/theme/ClayInput.kt app/src/test/java/com/openfang/mobile/ui/theme/*Test.kt
git commit -m "feat(ui): add basic clay components (card, button, toggle, chip, input)"
```

---

### Task 3: Layout components — FloatingNavDock, SidebarDrawer, ModelSelector

**Files:**
- Create: `app/src/main/java/com/openfang/mobile/ui/theme/FloatingNavDock.kt`
- Create: `app/src/main/java/com/openfang/mobile/ui/theme/SidebarDrawer.kt`
- Create: `app/src/main/java/com/openfang/mobile/ui/theme/ModelSelector.kt`

**Interfaces:**
- Consumes: `PastelClayTheme`, `ClayCard`, `ClayButton`, `ClayChip` (Task 1-2)
- Produces: `FloatingNavDock(selectedTab, onTabClick)`, `SidebarDrawer(isOpen, onClose, content)`, `ModelSelector(selectedModel, onModelClick)`

- [ ] **Step 1: Write failing test for FloatingNavDock**

```kotlin
@Test fun floatingNavDock_tabClick_invokesCallback() {
    var selected = 0
    // Compose test: click tab 1, assert selected == 1
}
```

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.theme.FloatingNavDockTest"`
Expected: FAIL

- [ ] **Step 2: Implement `FloatingNavDock` di `FloatingNavDock.kt`**

Pill bar di bawah, `rgba(255,255,255,0.85)` backdrop blur, 3 tab (Chat, Provider, Pengaturan), tab aktif primary + background putih clay pill, tab non-aktif slate-500.

- [ ] **Step 3: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.theme.FloatingNavDockTest"`
Expected: PASS

- [ ] **Step 4: Write failing test for SidebarDrawer**

```kotlin
@Test fun sidebarDrawer_backdropTap_closesDrawer() {
    // Compose test: open drawer, tap backdrop, assert isOpen == false
}
```

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.theme.SidebarDrawerTest"`
Expected: FAIL

- [ ] **Step 5: Implement `SidebarDrawer` di `SidebarDrawer.kt`**

Slide-left drawer (84% lebar, max 340dp), backdrop blur, close button, content slot. `AnimatedVisibility` + `Modifier.offset` untuk animasi slide.

- [ ] **Step 6: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.theme.SidebarDrawerTest"`
Expected: PASS

- [ ] **Step 7: Write failing test for ModelSelector**

```kotlin
@Test fun modelSelector_modelClick_invokesCallback() {
    // Compose test: click GPT-4o option, assert onModelClick called with "GPT-4o"
}
```

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.theme.ModelSelectorTest"`
Expected: FAIL

- [ ] **Step 8: Implement `ModelSelector` di `ModelSelector.kt`**

Sticky top bar, clay card dengan model aktif (ikon + nama + provider), dropdown panel dengan 4 opsi model (Claude 3.5 Sonnet, GPT-4o, Gemini 1.5 Pro, Llama 3.3 70B).

- [ ] **Step 9: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.theme.ModelSelectorTest"`
Expected: PASS

- [ ] **Step 10: Run all layout component tests**

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.theme.*"`
Expected: PASS

- [ ] **Step 11: Commit**

```bash
git add app/src/main/java/com/openfang/mobile/ui/theme/FloatingNavDock.kt app/src/main/java/com/openfang/mobile/ui/theme/SidebarDrawer.kt app/src/main/java/com/openfang/mobile/ui/theme/ModelSelector.kt app/src/test/java/com/openfang/mobile/ui/theme/*Test.kt
git commit -m "feat(ui): add layout components (floating nav dock, sidebar drawer, model selector)"
```

---

### Task 4: Composite components — MessageBubble, HandCard, PromptPill, ReactionButtons

**Files:**
- Create: `app/src/main/java/com/openfang/mobile/ui/theme/components/MessageBubbleClay.kt`
- Create: `app/src/main/java/com/openfang/mobile/ui/theme/components/HandCardClay.kt`
- Create: `app/src/main/java/com/openfang/mobile/ui/theme/components/PromptPill.kt`
- Create: `app/src/main/java/com/openfang/mobile/ui/theme/components/ReactionButtons.kt`

**Interfaces:**
- Consumes: `PastelClayTheme`, `ClayCard`, `ClayButton`, `ClayChip` (Task 1-2)
- Produces: `MessageBubbleClay`, `HandCardClay`, `PromptPill`, `ReactionButtons`

- [ ] **Step 1: Write failing test for MessageBubbleClay**

```kotlin
@Test fun messageBubbleClay_userMessage_usesPrimaryGradient() {
    // Compose test: user bubble has gradient background from primary
}
```

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.theme.components.MessageBubbleClayTest"`
Expected: FAIL

- [ ] **Step 2: Implement `MessageBubbleClay` di `MessageBubbleClay.kt`**

User bubble: gradient primary-to-light, rounded-3xl with rounded-tr-minus-md. AI bubble: putih clay card, rounded-3xl with rounded-tl-minus-md. System bubble: error container. Include avatar, timestamp, reaction buttons.

- [ ] **Step 3: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.theme.components.MessageBubbleClayTest"`
Expected: PASS

- [ ] **Step 4: Write failing test for HandCardClay**

```kotlin
@Test fun handCardClay_activateClick_invokesCallback() {
    // Compose test: click Activate button, assert onActivate called
}
```

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.theme.components.HandCardClayTest"`
Expected: FAIL

- [ ] **Step 5: Implement `HandCardClay` di `HandCardClay.kt`**

Clay card dengan nama hand, StatusPill (clay chip), metrics parse, tombol Activate/Pause (clay pill button), last run timestamp.

- [ ] **Step 6: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.theme.components.HandCardClayTest"`
Expected: PASS

- [ ] **Step 7: Implement `PromptPill` dan `ReactionButtons`** (sama pola: test → implement → verify)

- [ ] **Step 8: Run all composite component tests**

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.theme.components.*"`
Expected: PASS

- [ ] **Step 9: Commit**

```bash
git add app/src/main/java/com/openfang/mobile/ui/theme/components/MessageBubbleClay.kt app/src/main/java/com/openfang/mobile/ui/theme/components/HandCardClay.kt app/src/main/java/com/openfang/mobile/ui/theme/components/PromptPill.kt app/src/main/java/com/openfang/mobile/ui/theme/components/ReactionButtons.kt app/src/test/java/com/openfang/mobile/ui/theme/components/*Test.kt
git commit -m "feat(ui): add composite clay components (message bubble, hand card, prompt pill, reaction buttons)"
```

---

### Task 5: DashboardScreenClay

**Files:**
- Create: `app/src/main/java/com/openfang/mobile/ui/dashboard/DashboardScreenClay.kt`

**Interfaces:**
- Consumes: `DashboardVm` (via `hiltViewModel()`), `ClayCard`, `ClayButton`, `HandCardClay`, `FloatingNavDock` (Task 1-3)
- Produces: `DashboardScreenClay(vm: DashboardVm)` — Composable yang mengganti `DashboardScreen`

- [ ] **Step 1: Write failing test for DashboardScreenClay**

```kotlin
@Test fun dashboardScreenClay_daemonRunning_showsRunningStatus() {
    // Compose test with mocked DashboardVm: daemonRunning=true shows "● Running"
}
```

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.dashboard.DashboardScreenClayTest"`
Expected: FAIL

- [ ] **Step 2: Implement `DashboardScreenClay` di `DashboardScreenClay.kt`**

Header status daemon (Running/Stopped) dengan clay card. Kernel warning banner. Hands section: grid HandCardClay. Agents section: clay card per agent. Bottom: FloatingNavDock.

- [ ] **Step 3: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.dashboard.DashboardScreenClayTest"`
Expected: PASS

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/openfang/mobile/ui/dashboard/DashboardScreenClay.kt app/src/test/java/com/openfang/mobile/ui/dashboard/DashboardScreenClayTest.kt
git commit -m "feat(ui): add DashboardScreenClay with Pastel Clay design"
```

---

### Task 6: ChatScreenClay (dengan sidebar drawer + model selector)

**Files:**
- Create: `app/src/main/java/com/openfang/mobile/ui/chat/ChatScreenClay.kt`

**Interfaces:**
- Consumes: `ChatVm` (via `hiltViewModel()`), `SidebarDrawer`, `ModelSelector`, `MessageBubbleClay`, `ClayInput`, `PromptPill`, `ReactionButtons`, `FloatingNavDock` (Task 1-4)
- Produces: `ChatScreenClay(vm: ChatVm)` — Composable yang mengganti `ChatScreen`

- [ ] **Step 1: Write failing test for ChatScreenClay**

```kotlin
@Test fun chatScreenClay_streamingMessage_showsDelta() {
    // Compose test with mocked ChatVm: isBusy=true shows streaming indicator in clay bubble
}
```

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.chat.ChatScreenClayTest"`
Expected: FAIL

- [ ] **Step 2: Implement `ChatScreenClay` di `ChatScreenClay.kt`**

Sidebar drawer (kiri): profile, "Chat Baru", riwayat chat grouped, quota widget. Sticky model selector di atas. Message list: LazyColumn dengan MessageBubbleClay, streaming indicator, auth error banner. Prompt pills horizontal. Chat input dock di bawah: textarea, lampiran, mikrofon, kirim. Bottom: FloatingNavDock.

- [ ] **Step 3: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.chat.ChatScreenClayTest"`
Expected: PASS

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/openfang/mobile/ui/chat/ChatScreenClay.kt app/src/test/java/com/openfang/mobile/ui/chat/ChatScreenClayTest.kt
git commit -m "feat(ui): add ChatScreenClay with sidebar drawer and model selector"
```

---

### Task 7: LogsScreenClay

**Files:**
- Create: `app/src/main/java/com/openfang/mobile/ui/logs/LogsScreenClay.kt`

**Interfaces:**
- Consumes: `LogsVm` (via `hiltViewModel()`), `ClayCard`, `ClayChip`, `FloatingNavDock` (Task 1-2)
- Produces: `LogsScreenClay(vm: LogsVm)` — Composable yang mengganti `LogsScreen`

- [ ] **Step 1: Write failing test for LogsScreenClay**

```kotlin
@Test fun logsScreenClay_filterErrors_showsOnlyErrors() {
    // Compose test with mocked LogsVm: filter=Errors shows only error lines
}
```

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.logs.LogsScreenClayTest"`
Expected: FAIL

- [ ] **Step 2: Implement `LogsScreenClay` di `LogsScreenClay.kt`**

Tail log file dalam clay card per baris. Filter All/Errors (segmented control pill). Auto-refresh indikator. Bottom: FloatingNavDock.

- [ ] **Step 3: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.logs.LogsScreenClayTest"`
Expected: PASS

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/openfang/mobile/ui/logs/LogsScreenClay.kt app/src/test/java/com/openfang/mobile/ui/logs/LogsScreenClayTest.kt
git commit -m "feat(ui): add LogsScreenClay with Pastel Clay design"
```

---

### Task 8: SetupScreenClay

**Files:**
- Create: `app/src/main/java/com/openfang/mobile/ui/setup/SetupScreenClay.kt`

**Interfaces:**
- Consumes: `SetupVm` (via `hiltViewModel()`), `ClayCard`, `ClayButton`, `FloatingNavDock` (Task 1-2)
- Produces: `SetupScreenClay(vm: SetupVm)` — Composable yang mengganti `SetupScreen`

- [ ] **Step 1: Write failing test for SetupScreenClay**

```kotlin
@Test fun setupScreenClay_downloadComplete_showsDoneState() {
    // Compose test with mocked SetupVm: state=complete shows "Setup selesai"
}
```

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.setup.SetupScreenClayTest"`
Expected: FAIL

- [ ] **Step 2: Implement `SetupScreenClay` di `SetupScreenClay.kt`**

Alur one-time: download → unpack → verify → smoke test. Progress indicator dengan clay progress bar. Tombol retry (clay pill button). State dari `SetupStateStore`. Bottom: FloatingNavDock.

- [ ] **Step 3: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.ui.setup.SetupScreenClayTest"`
Expected: PASS

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/openfang/mobile/ui/setup/SetupScreenClay.kt app/src/test/java/com/openfang/mobile/ui/setup/SetupScreenClayTest.kt
git commit -m "feat(ui): add SetupScreenClay with Pastel Clay design"
```

---

### Task 9: MainActivity integration + Font + Dark mode

**Files:**
- Modify: `app/src/main/java/com/openfang/mobile/MainActivity.kt`
- Create: `app/src/main/res/values/fonts.xml`
- (Catatan: TTF font files Plus Jakarta Sans perlu ditempatkan di `res/font/` — binary, tidak dapat dibuat oleh agent)

**Interfaces:**
- Consumes: `DashboardScreenClay`, `ChatScreenClay`, `LogsScreenClay`, `SetupScreenClay`, `FloatingNavDock`, `PastelClayTheme` (Task 1-8)
- Produces: `MainActivity` dengan Clay screens, FloatingNavDock, dark mode toggle, SharedPreferences theme persistence

- [ ] **Step 1: Write failing test for MainActivity dark mode persistence**

```kotlin
@Test fun mainActivity_darkModePreference_persistsAcrossRecreation() {
    // Robolectric test: set isDark=true, recreate activity, assert theme is dark
}
```

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.MainActivityTest"`
Expected: FAIL

- [ ] **Step 2: Implement font.xml di `res/values/fonts.xml`**

Definisikan font family Plus Jakarta Sans dengan fontWeight mapping (400, 600, 700, 800). Note: TTF files harus di-copy manual ke `res/font/`.

- [ ] **Step 3: Modify `MainActivity.kt`**

Ganti `AppRoot()` dan `NavTab()` dengan Clay screens sesuai selectedTab. Tambahkan `PastelClayTheme(isDark = isDark)` wrapper. Tambahkan dark mode toggle state di `SharedPreferences`. FloatingNavDock sebagai bottom nav.

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.openfang.mobile.MainActivityTest"`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/openfang/mobile/MainActivity.kt app/src/main/res/values/fonts.xml app/src/test/java/com/openfang/mobile/MainActivityTest.kt
git commit -m "feat(ui): integrate Clay screens into MainActivity with dark mode support"
```

---

### Task 10: Build verification + existing tests

**Files:**
- Modify: (tidak ada — verifikasi saja)

**Interfaces:**
- Consumes: semua file dari Task 1-9
- Produces: APK debug build berhasil, semua unit test esist lolos

- [ ] **Step 1: Build debug APK**

Run: `./gradlew :app:assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 2: Run all existing unit tests**

Run: `./gradlew :app:testDebugUnitTest`
Expected: semua test lolos (termasuk test esist yang tidak diubah)

- [ ] **Step 3: Commit**

```bash
git add -A
git commit -m "feat(ui): complete Pastel Clay UI conversion with dark mode"
```

---

## Self-Review

1. **Spec coverage:** Setiap section di design doc (theme, components, 4 screens, MainActivity, dark mode) memiliki task yang sesuai. Tidak ada gap.
2. **Step scan:** Setiap langkah menurunkan ke satu aksi yang dapat ditulis (test → implement → verify → commit). Tidak ada baris yang mengambil keputusan tanpa spesifikasi ("TDD", "handle edge cases").
3. **Type consistency:** `PastelClayTheme`, `ClayCard`, `ClayButton`, `ClayToggle`, `ClayChip`, `ClayInput`, `FloatingNavDock`, `SidebarDrawer`, `ModelSelector`, `MessageBubbleClay`, `HandCardClay`, `PromptPill`, `ReactionButtons` — semua nama konsisten di seluruh task. `hiltViewModel()` digunakan di semua `*ScreenClay`.
4. **Review Focus:** 5 input class/failure mode (dark mode persistence, shadow density, drawer backdrop, font fallback, chat streaming) masing-masing memiliki test di task yang memiliki kode terkait.
5. **Proportion:** Plan ~80 baris untuk design doc ~150 baris. Rasio sebanding. Tidak ada code block sebagai transcript — hanya signature dan test assertions.

---

Plan disimpan di: `docs/superpowers/plans/2026-07-01-ui-conversion-pastel-clay.md`

Silakan review apakah plan ini menangkap semua yang Anda inginkan. Pilih metode eksekusi:

- **Subagent-driven** — Subagent baru implement setiap task, reviewer fresh memeriksa sebelum task berikutnya, lalu review whole-branch di akhir. paling menyeluruh; biaya konteks baru per task dan per review.
- **Native** — Saya implement setiap task sendiri di sesi ini, lalu satu fresh reviewer di model terbaik memeriksa seluruh branch di akhir. paling cepat dan murah; review independen hanya di akhir.

Rekomendasi saya: **Native**, karena task-task ini bergantung erat satu sama lain melalui interfaces yang sudah ditentukan (misal ClayCard harus ada sebelum DashboardScreenClay), ada sekitar 10 task, dan kesalahan yang di-ship dapat dengan mudah dideteksi melalui build + existing unit test.

Apakah plan ini sesuai keinginan Anda, dan metode eksekusi mana yang dipilih?
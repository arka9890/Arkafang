# Design Doc: Konversi UI openfang-android ke Pastel Clay (Stitch)

**Tanggal:** 2026-07-01  
**Status:** Draft — menunggu approval  
**Lokasi folder Stitch:** `stitch_tampilan_chat_ai_multi_fitur/`  
**Design system yang dipilih:** Pastel Clay (termasuk varian gelap / dark mode)

---

## 1. Intent dan Why

Proyek `openfang-android` saat ini menggunakan UI Jetpack Compose dengan tema Material3 default (gelap, slate). Pengguna ingin mengganti seluruh UI tersebut dengan desain dari folder `stitch_tampilan_chat_ai_multi_fitur` (disebut "Stitch"), menggunakan design system **Pastel Clay**.

Tujuan: aplikasi OpenFang Android mendapatkan tampilan yang konsisten, modern, dan menarik secara visual sesuai mockup Stitch, sambil tetap mempertahankan semua fungsionalitas inti yang sudah ada (daemon Proot, chat streaming, dashboard, setup one-time, token enkripsi, dll.).

---

## 2. Goals, Non-Goals, Anti-Goals

### Goals
- Semua 4 layar esist (Dashboard, Chat, Logs, Setup) dikonversi ke Compose dengan desain Pastel Clay.
- Navigasi bottom bar diganti dengan **Floating Navigation Dock** (pill-shaped, frosted glass) seperti mockup Stitch.
- Sidebar drawer (chat history, model selector, quota widget) diterapkan di layar Chat.
- Design system Pastel Clay diterapkan secara konsisten: warna, tipografi (Plus Jakarta Sans), bentuk pill, claymorphism shadows.
- Semua logika ViewModel, Hilt DI, Ktor API, ProotOrchestrator, TokenStore tetap utuh — hanya yang berubah adalah lapisan UI (Composable).
- Aplikasi tetap buildable dengan Gradle 8.13, AGP 8.5.2, Kotlin 2.0.20.

### Non-Goals
- Tidak menambah fitur baru di luar apa yang sudah ada di ViewModel saat ini.
- Tidak mengubah arsitektur MVVM-like atau Hilt DI.
- Tidak mengubah logika Proot, OpenFang daemon, atau API client.
- Tidak menambah database Room/SQLite di sisi Android.
- Tidak mengubah file Rust (`crates/`), SDK Python/JS, atau dokumentasi di luar Android.

### Anti-Goals
- Tidak ada HTML/Tailwind yang dikompile di Android — semua dikonversi secara manual ke Compose.
- Tidak menggunakan WebView untuk menampilkan mockup asli.
- Tidak mengganti tema Material3 sepenuhnya — kita akan buat `PastelClayTheme` yang melingkupi atau mengganti `MaterialTheme` untuk UI baru.
- Tidak menghapus layar Logs atau Setup — hanya retampilan.

---

## 3. Constraints

| Constraint | Keterangan |
|------------|------------|
| Bahasa | Kotlin 100%, Jetpack Compose 100% |
| UI Framework | Compose Material3 sebagai dasar, tetapi override dengan Pastel Clay theme |
| Min SDK | 26 (Android 8.0) |
| Font | Plus Jakarta Sans (harus ditambahkan sebagai resource font di `res/font/`) |
| Icon | Material Symbols Outlined (sudah tersedia via Material3 icons) |
| Warna (Terang) | Palet Pastel Clay dari `DESIGN.md` (surface `#fbf8ff`, primary `#4361EE`, dll.) |
| Warna (Gelap) | Varian gelap dari Pastel Clay: surface `#0e193e`, primary `#bac3ff` (inverse-primary), on-surface `#efefff`, dll. — lihat Tabel 3.1 |
| Bentuk | Pill-shaped (9999px / full) untuk buttons, chips, toggles; rounded-3xl (24-32dp) untuk cards |
| Elevation | Claymorphism: drop shadow + inset highlight + inset shadow (harus diimplementasi sebagai custom Compose modifier) |
| Navigasi | Single activity, bottom navigation dock (3 tab: Chat, Provider, Pengaturan) |
| Hilt | `hiltViewModel()` tetap digunakan di semua screen |
| Tema switching | `PastelClayTheme` mendukung `isDark` parameter; preferensi disimpan di `SharedPreferences` |

### Tabel 3.1: Warna Pastel Clay — Terang vs Gelap

| Warna | Terang | Gelap |
|-------|--------|-------|
| surface | `#fbf8ff` | `#0e193e` |
| surface-dim | `#d1d8ff` | `#1a2a5c` |
| surface-container-lowest | `#ffffff` | `#16214b` |
| surface-container-low | `#f3f2ff` | `#1e3066` |
| surface-container | `#ecedff` | `#263a7d` |
| surface-container-high | `#e4e7ff` | `#2e4696` |
| surface-container-highest | `#dde1ff` | `#3a54b3` |
| on-surface | `#0e193e` | `#efefff` |
| on-surface-variant | `#444655` | `#c4c5d7` |
| inverse-surface | `#242e55` | `#fbf8ff` |
| inverse-on-surface | `#efefff` | `#0e193e` |
| outline | `#747686` | `#5a6882` |
| outline-variant | `#c4c5d7` | `#3a4a7c` |
| surface-tint | `#2e4edc` | `#bac3ff` |
| primary | `#4361EE` | `#bac3ff` |
| on-primary | `#ffffff` | `#0e193e` |
| primary-container | `#4361EE` | `#2e4edc` |
| on-primary-container | `#f4f2ff` | `#1a2a5c` |
| inverse-primary | `#bac3ff` | `#4361EE` |
| secondary | `#3f5aab` | `#8ea8ff` |
| on-secondary | `#ffffff` | `#0e193e` |
| secondary-container | `#8ea8ff` | `#3f5aab` |
| on-secondary-container | `#1a398a` | `#0e193e` |
| tertiary | `#435878` | `#5c7091` |
| on-tertiary | `#ffffff` | `#f0f3ff` |
| tertiary-container | `#5c7091` | `#435878` |
| on-tertiary-container | `#f0f3ff` | `#1a2a5c` |
| error | `#ba1a1a` | `#ff8a80` |
| on-error | `#ffffff` | `#0e193e` |
| error-container | `#ffdad6` | `#ba1a1a` |
| on-error-container | `#93000a` | `#0e193e` |
| background | `#fbf8ff` | `#0e193e` |
| on-background | `#0e193e` | `#efefff` |
| surface-variant | `#dde1ff` | `#1e3066` |

**Catatan:** Dark mode palette didapatkan dengan inversi warna primer dari `DESIGN.md` (surface terang → surface gelap, primary terang → inverse-primary gelap, dst.) untuk memastikan kontras WCAG AAA tetap terpenuhi.

---

## 4. Approaches

Dua opsi pertimbangan:

### Opsi A: Buat `PastelClayTheme` terpisah (direkomendasikan)
Buat Compose theme custom yang meng-define `ColorScheme`, `Typography`, dan `Shapes` sesuai Pastel Clay. Semua screen baru menggunakan `PastelClayTheme { ... }` alih-alih `MaterialTheme { ... }`. Claymorphism shadows diimplementasi sebagai custom `Modifier` (drawBehind dengan `drawShadow` + `inset` untuk highlight).

**Kelebihan:** Kontrol penuh atas desain, konsisten dengan DESIGN.md, mudah dipelihara.  
**Kekurangan:** Perlu menulis custom shadow modifier dan theme dari nol.

### Opsi B: Override MaterialTheme existentes
Ubah warna, typografi, dan shapes di `MaterialTheme` yang sudah ada di `themes.xml` / `Theme.kt`.

**Kelebihan:** Lebih cepat, tidak perlu membuat theme baru.  
**Kekurangan:** Material3 tidak native mendukung claymorphism (inset shadow, pillow forms), akan terlihat "palsu" dan tidak konsisten dengan mockup Stitch.

**Saya merekomendasikan Opsi A** karena desain Pastel Clay sangat spesifik (claymorphism, pill shapes, frosted glass) dan Material3 override tidak akan cukup akurat.

---

## 5. Struktur File yang Akan Dibuat / Diubah

### File baru (UI + Theme)
```
app/src/main/java/com/openfang/mobile/ui/theme/
├── PastelClayTheme.kt      ← Theme definition (warna, tipografi, shapes)
├── ClayShadow.kt           ← Custom modifier untuk claymorphism shadow
├── ClayCard.kt             ← Clay card component
├── ClayButton.kt           ← Pill button
├── ClayToggle.kt           ← Pill toggle/switch
├── ClayChip.kt             ← Pill chip/tag
├── ClayInput.kt            ← Recessed input field
├── FloatingNavDock.kt      ← Bottom navigation dock (pill, frosted)
├── SidebarDrawer.kt        ← Slide-left drawer (chat history, profile, quota)
├── ModelSelector.kt        ← Sticky top model selector bar
└── components/
    ├── MessageBubbleClay.kt
    ├── HandCardClay.kt
    ├── PromptPill.kt
    └── ReactionButtons.kt
```

### File baru (Screens)
```
app/src/main/java/com/openfang/mobile/ui/
├── dashboard/DashboardScreenClay.kt   ← Ganti DashboardScreen.kt
├── chat/ChatScreenClay.kt             ← Ganti ChatScreen.kt (dengan drawer + model selector)
├── logs/LogsScreenClay.kt             ← Ganti LogsScreen.kt
├── setup/SetupScreenClay.kt           ← Ganti SetupScreen.kt
└── MainActivity.kt                    ← Diubah (gunakan Clay screens + FloatingNavDock)
```

### File yang diubah
- `MainActivity.kt` — ganti `AppRoot()` dan `NavTab()` dengan `FloatingNavDock`, panggil `*ScreenClay` alih-alih `*Screen`
- `app/build.gradle.kts` — tambahkan font resource jika perlu (Plus Jakarta Sans)

### File yang TIDAK diubah (logika inti tetap)
- `ChatVm.kt`, `DashboardVm.kt`, `LogsVm.kt`, `SetupVm.kt`
- `ProotOrchestrator.kt`, `ProotService.kt`, `StatusNotifier.kt`
- `OpenFangApiClient.kt`, `Dtos.kt`, `OpenFangApi.kt`
- `TokenStore.kt`, `SetupStateStore.kt`, `MirrorRegistry.kt`, `EnvironmentDownloader.kt`, `EnvironmentUnpacker.kt`, `IntegrityVerifier.kt`
- `AppModule.kt` (Hilt DI)

---

## 6. Desain Utama (Screen by Screen)

### 6.1 Floating Navigation Dock (ganti Bottom Nav 3-tab)
- Pill-shaped bar di bawah layar, `rgba(255,255,255,0.85)` dengan `backdropFilterBlur`
- 3 tab: Chat, Provider, Pengaturan (ikon Material Symbols + label)
- Tab aktif: primary color `#4361EE`, background putih clay pill
- Tab non-aktif: slate-500, transparan

### 6.2 Dashboard Screen (Clay)
- Header status daemon (Running/Stopped) dengan clay card
- Kernel warning banner
- Hand grid: setiap Hand dalam clay card dengan StatusPill (clay chip), metrics, tombol Activate/Pause (clay pill button)
- Agent list: clay card per agent
- Semua surface: surface-container-low `#f3f2ff` atau surface `#fbf8ff`

### 6.3 Chat Screen (Clay) — dengan Sidebar Drawer
- **Sidebar drawer** (slide kiri, 84% lebar, max 340dp):
  - Profile header (nama, PRO PLAN badge)
  - Tombol "Chat Baru" (clay primary button)
  - Riwayat chat dikelompokkan: Hari Ini, Kemarin, 7 Hari Terakhir (clay card per item)
  - Footer: quota komputasi (progress bar), tombol Arsip dan Registry Model
- **Sticky top model selector:** clay card dengan model aktif (Claude 3.5 Sonnet), dropdown pilihan model
- **Message bubbles:** user bubble (gradient dari primary), AI bubble (putih clay card), dengan avatar, timestamp, reaksi (suka/tidak suka, generate ulang), token count
- **Prompt pills:** horizontal scrollable clay chips di bawah pesan
- **Chat input dock:** sticky bawah, textarea multiline, tombol lampiran, mikrofon, kirim (gradient primary)

### 6.4 Logs Screen (Clay)
- Tail log file dengan clay card per baris log
- Filter All/Errors (segmented control pill)
- Auto-refresh indikator

### 6.5 Setup Screen (Clay)
- Alur one-time: download → unpack → verify → smoke test
- Progress indicator dengan clay progress bar
- Tombol retry (clay pill button)
- State disimpan di `SetupStateStore` (tidak berubah)

---

## 7. Yang Dihasilkan (Deliverables)

1. `docs/openfang-android/ui-conversion-pastel-clay-design.md` (ini) — **disetujui oleh pengguna**
2. File-file Compose baru di `app/src/main/java/com/openfang/mobile/ui/theme/`
3. File-file `*ScreenClay.kt` untuk 4 layar
4. Perubahan di `MainActivity.kt`
5. Font resource Plus Jakarta Sans di `res/font/`
6. Build berhasil (`./gradlew :app:assembleDebug`)
7. Semua unit test esist tetap lolos (karena ViewModel tidak berubah)

---

## 8. Yang Ditelampoki dari Pekerjaan Ini

- Tidak ada perubahan pada file Rust (`crates/`)
- Tidak ada perubahan pada SDK Python/JS (`sdk/`)
- Tidak ada fitur baru (hanya retampilan UI)
- Tidak ada pengujian UI/Instrumented baru (kecuali test esist yang sudah ada)
- Tidak ada perubahan pada `AndroidManifest.xml` atau permission

---

## 9. Rencana Kerja (Setelah Approval)

1. Buat `PastelClayTheme.kt` + `ClayShadow.kt` + komponen dasar (Card, Button, Toggle, Chip, Input)
2. Buat `FloatingNavDock.kt`, `SidebarDrawer.kt`, `ModelSelector.kt`
3. Buat `DashboardScreenClay.kt`, `ChatScreenClay.kt`, `LogsScreenClay.kt`, `SetupScreenClay.kt`
4. Ubah `MainActivity.kt` untuk menggunakan Clay screens + FloatingNavDock
5. Tambah font Plus Jakarta Sans
6. Build dan verifikasi `./gradlew :app:assembleDebug`
7. Jalankan unit test esist: `./gradlew :app:testDebugUnitTest`

---

Mohon **approve** design doc ini sebelum saya mulai menulis kode. Apakah ada yang ingin Anda ubah atau tambahkan?
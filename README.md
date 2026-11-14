# OpenFang on Android (Proot)

An Android APK that runs the [OpenFang](https://github.com/RightNow-AI/openfang)
agent operating system inside a Proot Ubuntu rootfs, directly on your
phone. No root, no server — just an APK + a one-time environment
download (~150 MB).

## What you get

- **APK** (~30 MB) — small, sideload-friendly
- **Setup flow** — first run downloads & unpacks the environment
- **Dashboard** — see agents, the 7 autonomous Hands, live status
- **Chat** — talk to any agent (OpenAI-compatible API)
- **Hands control** — activate / pause from your phone
- **Logs** — tail the OpenFang log file with level filter
- **Foreground service** — daemon stays alive when the app is
  backgrounded; tap the notification to get back

## Prerequisites (phone)

- **Android 8.0+ (API 26)**
- **arm64 (aarch64)** CPU — no 32-bit support
- **≥ 500 MB free storage** (env unpacks to ~250 MB)
- **≥ 2 GB RAM** (OpenFang idle ~40 MB, active Hands ~200+ MB)
- **Internet** during first-run setup only
- **Kernel ≥ 4.14** recommended (older kernels may have limited
  Proot features)

## Build the APK

### 1. Fork + clone

```bash
git clone https://github.com/<you>/openfang-android
cd openfang-android
```

### 2. Generate the env release

```bash
# Generate the 4 environment assets (Ubuntu rootfs + OpenFang binary
# + sha256sums + setup.env)
./env-release/build-mirror.sh v0.6.9

# Publish to a GitHub Release on your repo
./env-release/push-release.sh v0.6.9
```

### 3. Set signing secrets

Generate a keystore:

```bash
keytool -genkeypair -alias openfang \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -keystore keystore.jks \
  -storepass "<your-store-pw>" -keypass "<your-key-pw>"
```

Then in GitHub → Settings → Secrets → Actions, add:

| Secret | Value |
|---|---|
| `APK_SIGNING_KEY` | `base64 < keystore.jks` (single line) |
| `KEY_ALIAS` | `openfang` |
| `KEY_STORE_PASSWORD` | your store password |
| `KEY_PASSWORD` | your key password |

### 4. Build via GitHub Actions

Push to `main` (or trigger a manual workflow run). The
`build-apk.yml` workflow:

1. Downloads NDK
2. Cross-compiles `proot` for arm64 (`compile-proot.yml`)
3. `./gradlew :app:assembleDebug` (unsigned, for dev)
4. `./gradlew :app:assembleRelease` with your signing key
5. Uploads the APK as an artifact **and** as a release asset

Download from **Actions → latest run → artifacts** (90-day retention)
or from the auto-published **Releases** page (permanent).

### 5. Sideload

```bash
adb install openfang-android.apk
# or copy the APK to your phone and open it (allow "unknown sources")
```

## First run

1. Open the app → **Setup** screen appears
2. Tap **Start setup** — downloads ~150 MB from:
   1. `openfang.sh` (official)
   2. Your GitHub Release (fallback)
   3. A user-configured URL (if set in `mirrors.json`)
3. On success → **Dashboard**
4. Tap **Start** → Proot launches, OpenFang daemon starts
5. A persistent notification appears; tap it to return to the app

## Update OpenFang

```bash
./env-release/build-mirror.sh v0.7.0
./env-release/push-release.sh v0.7.0
# Bump mirrors.json to the new tag, push to main
# Next APK build pulls the new env; the app detects the version bump
# on launch and re-downloads
```

## Troubleshooting

See [`docs/troubleshooting.md`](docs/troubleshooting.md).

## Architecture (one paragraph)

The APK is a thin Kotlin/Compose wrapper. It downloads a Proot Ubuntu
rootfs + the pinned OpenFang binary on first run, unpacks them into
app-specific storage, and execs an embedded cross-compiled `proot`
binary to launch `openfang start` inside the rootfs. A foreground
service holds the daemon alive; a Ktor client talks to
`127.0.0.1:4200` for dashboard/chat. No auto-start on boot — start
is manual from the app (by design; see the spec's §3 non-goals).

## License

MIT (matches OpenFang). Proot is Apache-2.0.

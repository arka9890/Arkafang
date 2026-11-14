# env-release

Two scripts that publish the Proot environment assets to a GitHub
Release on this repo. The APK's `mirrors.json` points at this
release URL as the fallback mirror (after the official `openfang.sh`).

## Prerequisites

- `curl`, `xz`, `gzip`, `sha256sum`, `tar` (standard on macOS/Linux)
- GitHub CLI (`gh`) authenticated with write access to this repo
- Internet (downloads ~100–150 MB total)

## Workflow

1. **Generate the assets**
   ```bash
   ./env-release/build-mirror.sh v0.6.9
   ```
   This downloads the Ubuntu aarch64 rootfs + OpenFang binary, computes
   `sha256sums.txt`, and writes `setup.env`. Output is in `env-release/dist/`.

2. **Upload to a GitHub Release**
   ```bash
   ./env-release/push-release.sh v0.6.9
   ```
   Creates a release tag `v0.6.9` with the 4 assets.

3. **Bump the tag in the APK**

   Edit `app/src/main/res/raw/mirrors.json`:
   ```json
   { "name": "github-releases",
     "base": "https://github.com/arka9890/openfang-android/releases/download/v0.6.9/" }
   ```
   and set `"openfangTag": "v0.6.9"`.

4. **Push + CI builds the APK**

   Push to `main`. GitHub Actions builds a signed APK (or an unsigned
   debug APK if no signing secrets are set). Download from the
   Actions artifact or the auto-published release asset.

## Releasing a new OpenFang version

1. Run `./env-release/build-mirror.sh v0.7.0`
2. Run `./env-release/push-release.sh v0.7.0`
3. Update `mirrors.json` to point at the new tag
4. Push — the next APK build embeds the new env

## Troubleshooting

- **`curl` fails on openfang.sh** — the official mirror may not have
  the tag yet. Build the OpenFang binary from source and place it
  manually in `dist/openfang-aarch64.tar.gz`.
- **Ubuntu rootfs URL changed** — override with
  `UBUNTU_AARCH64_URL=<url> ./env-release/build-mirror.sh`.

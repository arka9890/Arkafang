#!/usr/bin/env bash
#
# env-release/push-release.sh
#
# Upload the dist/ assets to a GitHub Release on this repo.
#
# Usage:
#   ./env-release/build-mirror.sh v0.6.9
#   ./env-release/push-release.sh v0.6.9
#
# Requires: `gh` CLI, authenticated with repo write access.

set -euo pipefail

TAG="${1:?usage: push-release.sh <tag> (e.g. v0.6.9)}"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
DIST="$SCRIPT_DIR/dist"

[ -d "$DIST" ] || { echo "run build-mirror.sh first"; exit 1; }
command -v gh >/dev/null || { echo "install the GitHub CLI (gh)"; exit 1; }
gh auth status || { echo "gh not authenticated — run: gh auth login"; exit 1; }

# Repo is inferred from git remote
REPO="$(git -C "$SCRIPT_DIR/.." remote get-url origin | sed 's|.*github.com/||;s|\.git$||')"
echo ">> publishing $TAG to $REPO"

gh release create "$TAG" \
  --title "OpenFang environment $TAG" \
  --notes "Environment assets for the OpenFang Android APK.
- ubuntu-aarch64.tar.xz: Ubuntu 22.04 aarch64 rootfs for Proot
- openfang-aarch64.tar.gz: OpenFang binary pinned to $TAG
- sha256sums.txt: SHA-256 of the two tarballs
- setup.env: config the APK parses at runtime" \
  --target main \
  "$DIST"/ubuntu-aarch64.tar.xz \
  "$DIST"/openfang-aarch64.tar.gz \
  "$DIST"/sha256sums.txt \
  "$DIST"/setup.env

echo ">> done. Update mirrors.json in the APK to point at this tag:"
echo "   https://github.com/$REPO/releases/download/$TAG/"

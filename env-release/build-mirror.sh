#!/usr/bin/env bash
#
# env-release/build-mirror.sh
#
# Generate the 4 environment assets that get uploaded to a GitHub Release:
#   - ubuntu-aarch64.tar.xz    (Ubuntu aarch64 rootfs for Proot)
#   - openfang-aarch64.tar.gz  (OpenFang binary pinned to a tag)
#   - sha256sums.txt           (SHA-256 of the two tarballs)
#   - setup.env                 (key=value config for the APK)
#
# Usage:
#   ./env-release/build-mirror.sh [TAG]
#
#   TAG defaults to v0.6.9. The OpenFang binary is pulled from the
#   official release mirror; the Ubuntu rootfs from proot-distro's
#   upstream.
#
# Output: env-release/dist/
#
# Requires: curl, xz, gzip, sha256sum, tar

set -euo pipefail

TAG="${1:-v0.6.9}"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
DIST="$SCRIPT_DIR/dist"
WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

mkdir -p "$DIST"
echo ">> generating OpenFang env for tag $TAG into $DIST"

# 1) OpenFang binary (aarch64)
echo ">> downloading OpenFang $TAG (aarch64)"
curl -fsSL "https://openfang.sh/download/${TAG}/openfang-aarch64.tar.gz" \
  -o "$WORK/openfang-aarch64.tar.gz"
# Re-pack into dist (we keep the same name the APK expects)
cp "$WORK/openfang-aarch64.tar.gz" "$DIST/openfang-aarch64.tar.gz"

# 2) Ubuntu aarch64 rootfs
echo ">> downloading Ubuntu 22.04 aarch64 rootfs"
# proot-distro's official rootfs: https://proot-distro.dev/get-rootfs/
# We pull the amd64 one and re-pack — but on an aarch64 host we can
# just use the aarch64 rootfs directly.
UBUNTU_SRC="${UBUNTU_AARCH64_URL:-https://proot-distro.dev/rootfs/ubuntu-22.04-aarch64.tar.xz}"
curl -fsSL "$UBUNTU_SRC" -o "$DIST/ubuntu-aarch64.tar.xz"

# 3) sha256sums.txt
echo ">> writing sha256sums.txt"
( cd "$DIST" && sha256sum ubuntu-aarch64.tar.xz openfang-aarch64.tar.gz > sha256sums.txt )

# 4) setup.env
echo ">> writing setup.env"
cat > "$DIST/setup.env" <<EOF
openfangTag=$TAG
listenPort=4200
bindPaths=/dev,/proc,/sys,/sys/.
EOF

echo ">> done. Assets in $DIST:"
ls -lh "$DIST"

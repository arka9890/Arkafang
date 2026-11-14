#!/usr/bin/env bash
#
# scripts/build-proot-android.sh
#
# Cross-compiles proot 5.2.x for Android arm64 using the NDK clang.
# Output: app/src/main/assets/proot/proot (aarch64 static binary).
#
# Usage (in CI or locally):
#   export NDK=/path/to/ndk
#   ./scripts/build-proot-android.sh
#
# Requires:
#   - NDK with toolchain: aarch64-linux-android26-clang
#   - cmake, make, git
#
# The binary is embedded into the APK at build time; it is NOT
# fetched from the internet at runtime.

set -euo pipefail

NDK="${NDK:-$ANDROID_NDK_HOME}"
[ -n "$NDK" ] || { echo "NDK env var not set"; exit 1; }
API=26
TOOLCHAIN="$NDK/toolchains/llvm/prebuilt/$(uname -s | tr '[:upper:]' '[:lower:]')-x86_64"
CC="aarch64-linux-android${API}-clang"
[ -d "$TOOLCHAIN" ] || { echo "NDK toolchain not found at $TOOLCHAIN"; exit 1; }

WORKDIR="$(mktemp -d)"
trap 'rm -rf "$WORKDIR"' EXIT
cd "$WORKDIR"

echo ">> cloning proot 5.2.0"
git clone --depth=1 --branch v5.2.0 https://git.proot-emu.me/proot.git proot-src
cd proot-src

echo ">> building proot for Android arm64"
make CC="$CC" \
     CFLAGS="--target=aarch64linux-android -D__ANDROID_API__=${API} -O2 -static" \
     LDFLAGS="-static" \
     PREFIX="$WORKDIR/install" \
     DESTDIR="" \
     proot

cp "$WORKDIR/install/proot" "${REPO_ROOT:-$(cd ../.. && pwd)}/app/src/main/assets/proot/proot"
echo ">> built: app/src/main/assets/proot/proot"
file "app/src/main/assets/proot/proot" 2>/dev/null || true

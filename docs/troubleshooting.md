# OpenFang Android — Troubleshooting

## Setup fails

### "need 500 MB free, have X MB"
You don't have enough storage. Free up space or use a phone with more.
The env unpacks to ~250 MB; we check for 500 MB to leave room for
logs + SQLite + OS cache.

### "checksum mismatch: <file>"
The download was corrupted or a mirror served a stale file. Tap
**Retry** — the next attempt resumes from the last good byte (HTTP
Range) and re-verifies.

### "download failed" on all mirrors
Check your internet. The setup needs ~150 MB. If you're on a flaky
connection, the resume logic will retry per-mirror; if all three
fail, the connection is the issue.

## Daemon won't start

### "daemon exited immediately: <tail>"
The Proot exec failed. Look at the tail in the error — common causes:
- **Kernel too old** (see the yellow banner on the dashboard).
  Proot 5.2.x needs at least 3.18, recommended 4.14+.
- **Missing /dev entries** — make sure your phone has a working
  `/dev/null` (all real Androids do).
- **Binary not executable** — the unpack step sets +x; if you
  re-ran setup manually, re-run it.

### "env not set up; run setup first"
The `env/` directory doesn't exist. You skipped the setup screen.
Go back and run it.

## Daemon dies in background

The watchdog polls every 30 s. If Android kills the process (battery
optimization), the notification changes to "Stopped unexpectedly — tap
to restart". Tap it, or open the app and tap **Start** again.

To reduce this, go to **Settings → Battery → OpenFang → Unrestricted**
(on Samsung: **Settings → Battery → Battery usage → OpenFang →
Unrestricted**).

## Chat shows "Authentication failed"

The API token is missing or stale. The OpenFang daemon (v0.6.9+)
may require a Bearer token. Set it in:
- **Settings** in the app (when added in a future task), or
- Manually via `openfang.toml` inside the Proot rootfs (advanced).

Until then, this error means the token in `TokenStore` is empty or
was for an older OpenFang build.

## 401 after an OpenFang upgrade

The new binary expects a different auth scheme. The app surfaces this
as `AuthError` (Review Focus #5) — the chat shows a banner instead of
silently dropping. Re-set the token.

## Proot binary missing / not working

The APK ships with a cross-compiled `proot` in `assets/proot/proot`.
If it's missing (you built without the `compile-proot` workflow), the
daemon won't start. Re-run the CI to rebuild the binary.

## WhatsApp channel not connecting

The WhatsApp Web gateway (Node.js) is **not** bundled in this APK by
design (spec §3 non-goal). To use WhatsApp:
1. Run the gateway on a separate machine (laptop / VPS)
2. Point `WHATSAPP_WEB_GATEWAY_URL` at that machine
3. The OpenFang daemon in Proot will connect out to it

## Logs are empty

The log file is at `env/logs/openfang.log`. If it's empty, the daemon
isn't writing to it — either the daemon hasn't started, or Proot's
`-w /home/openfang` chroot means the log is inside the rootfs, not on
the Android host. The Logs screen reads the *in-memory* ring buffer
(1 MB) which is always up to date.

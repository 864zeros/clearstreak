# ClearStreak — Release Signing

The release build signs with a real key when one is supplied; otherwise it falls back to the debug
key so CI and local test builds stay green. **Nothing secret is ever committed** — `keystore.properties`,
`*.jks`, and `*.keystore` are gitignored.

Signing inputs are read in this priority order (see `app/build.gradle.kts`):
1. `keystore.properties` at the repo root (local machine)
2. Environment variables (CI secrets)
3. Debug key fallback (unsigned-for-Play; testing only)

## 1. Generate the upload keystore (one time)

> Recommended: enroll in **Play App Signing** (Google holds the real *app signing* key; you upload
> with an *upload* key). If the upload key is ever lost, Google can reset it — so this keystore is
> your **upload key**. Still: **back it up somewhere safe. Losing it (without Play App Signing) means
> you can never update the app.**

```bash
keytool -genkeypair -v \
  -keystore clearstreak-upload.jks \
  -alias clearstreak \
  -keyalg RSA -keysize 2048 -validity 10000
```

Keep `clearstreak-upload.jks` out of the repo (it's gitignored, but store it in a password manager /
secure backup, not just on disk).

## 2. Local signed build

Create `keystore.properties` at the repo root (gitignored):

```properties
storeFile=/absolute/path/to/clearstreak-upload.jks
storePassword=********
keyAlias=clearstreak
keyPassword=********
```

Then build the Play artifact (an **AAB**, which is what the Play Console requires — not an APK):

```bash
gradle bundleStoreRelease
# output: app/build/outputs/bundle/storeRelease/app-store-release.aab
```

## 3. CI signed build (GitHub Actions)

Add these repository **Secrets** (Settings → Secrets and variables → Actions):

| Secret | Value |
|---|---|
| `KEYSTORE_BASE64` | `base64 -w0 clearstreak-upload.jks` output |
| `KEYSTORE_PASSWORD` | store password |
| `KEY_ALIAS` | `clearstreak` |
| `KEY_PASSWORD` | key password |

Then add a signed-release job (or step) to `.github/workflows/build.yml`. The build reads env vars
directly, so decode the keystore to a file and point `KEYSTORE_FILE` at it:

```yaml
    - name: Decode upload keystore
      if: ${{ env.KEYSTORE_BASE64 != '' }}
      env:
        KEYSTORE_BASE64: ${{ secrets.KEYSTORE_BASE64 }}
      run: echo "$KEYSTORE_BASE64" | base64 -d > "$RUNNER_TEMP/upload.jks"

    - name: Build signed release bundle (AAB)
      if: ${{ env.KEYSTORE_BASE64 != '' }}
      env:
        KEYSTORE_FILE: ${{ runner.temp }}/upload.jks
        KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
        KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
        KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
      run: gradle bundleStoreRelease
```

Until the secrets exist, CI keeps building the debug-signed release exactly as today (green).

## 4. Verify a build is properly signed

```bash
# APK
apksigner verify --print-certs app/build/outputs/apk/store/release/*.apk
# AAB (via bundletool) or just confirm the upload succeeds in the Play Console
```

If the certificate shows the AndroidDebug CN, the keystore wasn't picked up — check that
`keystore.properties` exists at the **repo root** (not `app/`) or that the env vars are set.

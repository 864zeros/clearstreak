# ClearStreak

**864zeros LLC Local-First Utility | Pillar: Faith, Health & Growth**  
*Privacy-First Sobriety & Recovery Companion for Android*

> **"Your recovery is yours alone. We cannot see it, sell it, subpoena it, or lose it."**

---

## 🌟 Overview

ClearStreak counters the surveillance economics of incumbent recovery apps by operating entirely on-device: **no accounts, no cloud sync, no telemetry, and no subscriptions**. Recovery data is encrypted with hardware-bound keys via Android Keystore / StrongBox and never leaves the device.

Built strictly according to the **864zeros Build Kit** and the **OIA Design System v1.0**.

> **📍 Project docs & current state**
> - `ClearStreak_Spec_v2.md` — original product spec.
> - `ClearStreak_Blueprint_v1.md` — strategic blueprint (current direction authority).
> - `PROGRESS.md` — build state & full decision log. **Read this first to see what's actually built.**
>
> Order of authority where docs disagree: **PROGRESS + Blueprint > Spec**.

---

## 🛡️ Core Security Architecture

1. **Air-Gapped by Default**: `android.permission.INTERNET` is completely removed from the default `core` build flavor.
2. **Hardware-Bound Biometric Gate**: `BiometricPrompt` with `CryptoObject` gates all access to the encrypted database (`recovery_enc.db`). The master key is generated with `setUserAuthenticationRequired(true)` and `setInvalidatedByBiometricEnrollment(true)`, StrongBox-backed with a TEE fallback.
3. **Database Partitioning**:
   - `streak_core.db`: Plaintext SQLite for journey records, milestone tracking, and Glance widget synchronization.
   - `recovery_enc.db`: AES-256 encrypted with SQLCipher 4.x for check-in records and sensitive journal notes.
4. **Zero Cloud Backup**: Excluded via `android:allowBackup="false"` and `<data-extraction-rules>` (encrypted DB + secure prefs excluded from backup/transfer).
5. **Screen Protection**: `FLAG_SECURE` prevents OS-level screen captures and task-switcher snapshot leaks.

---

## 🧭 Recovery Model

- **Multi-Journey Tracker**: Track concurrent recoveries (substances, smoking, gambling, behavioral, custom) with independent start dates.
- **Recovery Ledger (`StreakCalculator`)**: current / longest / cumulative days, money saved, milestone progress.
- **Data Over Shame**: A slip resets the *active* streak but never erases history. Earned milestone badges are permanent; slip framing centers the preserved personal record and never uses "failure / lost / broken" language.
- **4-Tier Check-In**: 🟢 Clear · 🟡 Passing · 🟠 White-Knuckling · 🔴 Critical, with HALT context; 🔴 Critical routes to the Crisis Intercept.

---

## 🌬️ Acute-Craving Intervention (in progress)

The intervention model deliberately **avoids text "advice" and never suggests ingesting anything**. Instead it uses (see blueprint §2 and `PROGRESS.md`):

- **Somatosensory tools** — 60-second grounding timer and a 4-7-8 breathing circle with haptic pacing (built); a background "Pocket Anchor" and tactile box-breather are planned (Brick 4).
- **Visuospatial mini-games** — planned (Brick 5), and **gated off gaming/screen-recovery journeys** (per-journey `suppress_game_tools` flag) so the tool never feeds the habit it's treating.
- **Public-domain heritage content** — planned (Brick 6): Proverbs (KJV/WEB) and the Serenity Prayer. *(An AA Big Book vault is copyright-gated — pending legal confirmation; see `PROGRESS.md` §5.)*

> The earlier deterministic "coping cards" feature was **removed** in favor of this model.

---

## 🎨 OIA Design System v1.0

- **Palette**: Warm Neutrals (Cream `#F5F2ED`, Warm White `#FDFCFA`, Dark BG `#1A1A1A`) with Sage (`#8BA888`), Coral (`#E8A598`), Mustard (`#C9A86C`), Dusty Blue (`#7A8FA3`). **No pure `#000000` / `#FFFFFF`.**
- **Typography**: Rounded, relaxed type scale (SansSerif).
- **ADHD-Friendly UX**: one primary action per screen; large touch targets; non-shaming relapse model.
- **Crisis Intercept**: high-contrast fullscreen hub with native `tel://` dialers (Sponsor, Support Person, SAMHSA `1-800-662-4357`, 988 Lifeline), anonymous map queries (`geo:0,0?q=...`), and a 2-second long-press exit.

---

## 📱 Jetpack Glance Home-Screen Widget

- Reads streak counts and next milestone directly from sandboxed `SharedStreakStorage`.
- Tapping the widget opens the app.

---

## 🏗️ Build Flavors

- `coreDebug` / `coreRelease`: completely air-gapped, zero-network build.
- `storeDebug` / `storeRelease`: minimal networking strictly for optional Google Play Billing (IAP not yet implemented).

CI (`.github/workflows/build.yml`) builds **`assembleCoreDebug`** and **`assembleStoreRelease`** (the latter exercises R8 minify + resource shrink) on every push and uploads both APKs as artifacts.

---

## 📂 Project Structure

```
clearStreak/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/eight64zeros/clearstreak/
│       │   │   ├── ClearStreakApp.kt
│       │   │   ├── MainActivity.kt
│       │   │   ├── security/
│       │   │   │   ├── KeyStoreManager.kt
│       │   │   │   └── DatabasePassphraseProvider.kt
│       │   │   ├── database/
│       │   │   │   └── DatabaseManager.kt
│       │   │   ├── model/
│       │   │   │   ├── Enums.kt
│       │   │   │   ├── Journey.kt
│       │   │   │   ├── CheckIn.kt
│       │   │   │   ├── Milestone.kt
│       │   │   │   └── StreakStats.kt
│       │   │   ├── data/
│       │   │   │   ├── StreakCalculator.kt
│       │   │   │   ├── SharedStreakStorage.kt
│       │   │   │   └── EmergencyContactStorage.kt
│       │   │   ├── widget/
│       │   │   │   ├── ClearStreakWidget.kt
│       │   │   │   └── ClearStreakWidgetReceiver.kt
│       │   │   ├── navigation/
│       │   │   │   └── Screen.kt
│       │   │   └── ui/
│       │   │       ├── theme/       (Color, Theme, Type, Shape, Spacing)
│       │   │       ├── components/  (UrgePulseGrid, HaltTriggerRow, BreathingCircle,
│       │   │       │                 GroundingTimer, OIAButton, OIACard, OIATextField)
│       │   │       └── screens/     (Dashboard, CheckInModal, CrisisIntercept,
│       │   │                         JourneyDetail, Journal, Settings, AddJourney,
│       │   │                         BiometricLock)
│       │   └── res/                 (values, xml, drawable, mipmap, layout)
│       └── store/
│           └── AndroidManifest.xml  (adds INTERNET + BILLING)
├── .github/workflows/build.yml
├── ClearStreak_Spec_v2.md
├── ClearStreak_Blueprint_v1.md
├── PROGRESS.md
├── README.md
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
    └── libs.versions.toml
```

---

*Classification: 864zeros Internal — Ready for Build*

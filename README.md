# ClearStreak

**864zeros LLC Local-First Utility | Pillar: Faith, Health & Growth**  
*Privacy-First Sobriety & Recovery Companion for Android*

> **"Your recovery is yours alone. We cannot see it, sell it, subpoena it, or lose it."**

---

## 🌟 Overview

ClearStreak counters the surveillance economics of incumbent recovery apps by operating entirely on-device: **no accounts, no cloud sync, no telemetry, and no subscriptions**. Recovery data is encrypted with hardware-bound keys via Android Keystore / StrongBox and never leaves the device.

Built strictly according to the **864zeros Build Kit** and the **OIA Design System v1.0**.

> **📍 Project docs & current state**
> - `overview.html` — marketing-slanted product overview (features, privacy, philosophy).
> - `ClearStreak_Spec_v2.md` — original product spec.
> - `ClearStreak_Blueprint_v1.md` — strategic blueprint (current direction authority).
> - `PROGRESS.md` — build state & full decision log. **Read this first to see what's actually built.**
>
> Order of authority where docs disagree: **PROGRESS + Blueprint > Spec**.

---

## 🛡️ Core Security Architecture

1. **Air-Gapped by Default**: `android.permission.INTERNET` is completely removed from the default `core` build flavor. *(The `store` flavor adds INTERNET solely for Google Play Billing — no recovery data ever leaves the device.)*
2. **Hardware-Bound Biometric Gate**: `BiometricPrompt` with `CryptoObject` gates all access to the encrypted database (`recovery_enc.db`). The master key is generated with `setUserAuthenticationRequired(true)` and `setInvalidatedByBiometricEnrollment(true)`, StrongBox-backed with a TEE fallback.
3. **Database Partitioning**:
   - `streak_core.db`: Plaintext SQLite for journey records, milestone tracking, and Glance widget synchronization.
   - `recovery_enc.db`: AES-256 encrypted with SQLCipher 4.x for check-in records and sensitive journal notes.
4. **Zero Cloud Backup**: Excluded via `android:allowBackup="false"` and `<data-extraction-rules>` (encrypted DB + secure prefs excluded from backup/transfer).
5. **Screen Protection**: `FLAG_SECURE` prevents OS-level screen captures and task-switcher snapshot leaks.
6. **No Telemetry**: Zero analytics, zero trackers, zero ad SDKs.

> **Theme note:** the app is currently **light-only** (screens hardcode the OIA light palette). `OIATextField` forces dark input text so entries stay readable even on a dark-mode phone. A real light/dark system is deferred to the **864zeros-mobile-build-kit** (not built here).

---

## 🧭 Recovery Model

- **Multi-Journey Tracker**: Track concurrent recoveries — Alcohol / Drugs / Vape / Gambling / Behavioral / Custom (with a per-journey custom label) — each with an independent start date. No default journey is auto-seeded.
- **Recovery Ledger (`StreakCalculator`)**: current / longest / cumulative days (with sub-day hours), money saved, milestone progress.
- **Data Over Shame**: A slip resets the *active* streak but never erases history. Earned milestone badges are permanent; slip framing centers the preserved personal record and never uses "failure / lost / broken" language.
- **4-Tier Check-In**: 🟢 Clear · 🟡 Passing · 🟠 White-Knuckling · 🔴 Critical, with HALT context; 🔴 Critical routes to the Crisis Intercept.
- **Home is journeys-first**: journey cards + "Add Journey" sit at the top; the optional daily verse and passage cards are supplemental, below.

---

## 🌬️ Acute-Craving Intervention (built)

The intervention model deliberately **avoids text "advice" and never suggests ingesting anything** — any on-screen text is awareness, never a command:

- **Somatosensory tools** — a 60-second grounding timer and a 4-7-8 breathing circle with haptic pacing; a foreground **Pocket Anchor** and a **4×4×4×4 tactile box breather** (screen kept awake via `keepScreenOn`, no background service).
- **Visuospatial mini-games** — offline Canvas engines that crowd out craving imagery, **auto-withheld from gaming/screen-recovery journeys** (per-journey suppression) so a tool never feeds the habit it treats.
- **"Words for this moment"** — a contextual recovery passage on the Reset tab, routed by urge state.
- **The Science** — a plain-language screen explaining the games / breathing / Pocket Anchor with the research behind them.

> The earlier deterministic "coping cards" feature was **removed** in favor of this model.

---

## 📖 Reflect (Scripture + Recovery)

The former "Faith" tab is now **Reflect**, with two mirrored segments:

- **Scripture** — Proverb/verse-of-the-day (Public Domain WEB/KJV), the Serenity Prayer, and a verse calendar. 100% offline.
- **Recovery** — **130 re-authored recovery passages** written in plain, modern language, carrying the wisdom of early recovery literature. **Original content** (no verbatim reader, no clinical/legal gate); shipped as `assets/passages.json` with a passage-of-the-day, calendar, theme browse, and randomize.
- **Optional faith layer** — a Settings toggle adds an optional faith line under passages; off by default.

Content is served from lightweight in-memory JSON stores (`HeritageStore`, `PassageStore`) — no runtime DB (Android's system SQLite FTS5 proved unreliable).

---

## 💳 Monetization (store flavor)

- **Model**: **7-day free trial → $14.99 one-time unlock** via **Google Play Billing (8.3.0)**. No subscription, no account.
- **Product ID**: `clearstreak_unlock` (one-time / INAPP). Create it in the Play Console at $14.99.
- **Trial**: anchored to `PackageManager.firstInstallTime` (survives clear-data; resets on true reinstall — offline privacy stance rules out server enforcement).
- **Ethical line**: after the trial the app requires the unlock, **but the crisis Rescue hub always stays reachable** — no one is ever trapped behind a paywall in a hard moment, and nothing logged is deleted.
- **Flavor split**: real billing lives only in `store` (`billing/StoreBillingManager`); the air-gapped `core` flavor uses a stub that reports everything unlocked (`billing/CorePremiumManager`), with no billing dependency.

---

## 🎨 OIA Design System v1.0

- **Palette**: Warm Neutrals (Cream `#F5F2ED`, Warm White `#FDFCFA`) with Sage (`#8BA888`), Coral (`#E8A598`), Mustard (`#C9A86C`), Dusty Blue (`#7A8FA3`). **No pure `#000000` / `#FFFFFF`.**
- **Typography**: Rounded, relaxed type scale (SansSerif).
- **ADHD-Friendly UX**: one primary action per screen; large touch targets; non-shaming relapse model.
- **Crisis Intercept**: high-contrast fullscreen hub with native `tel://` dialers (Sponsor + Support Person always shown, SAMHSA `1-800-662-4357`, 988 Lifeline, Crisis Text Line, smoking/gambling quit-lines), anonymous map queries (`geo:0,0?q=...`), grounding timer + 4-7-8 breather, and a long-press exit.

---

## 📱 Jetpack Glance Home-Screen Widget

- Reads streak counts and next milestone directly from sandboxed `SharedStreakStorage`.
- Tapping the widget opens the app.

---

## 🏗️ Build Flavors

- `coreDebug` / `coreRelease`: completely air-gapped, zero-network build; premium always unlocked (operator/dogfood).
- `storeDebug` / `storeRelease`: minimal networking for Google Play Billing (7-day trial → $14.99 one-time unlock).

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
│       │   ├── assets/passages.json        (130 re-authored recovery passages)
│       │   ├── java/com/eight64zeros/clearstreak/
│       │   │   ├── MainActivity.kt
│       │   │   ├── billing/                 (PremiumManager, TrialStatus)  [store: StoreBillingManager]
│       │   │   ├── security/                (KeyStoreManager, DatabasePassphraseProvider)
│       │   │   ├── database/                (DatabaseManager)
│       │   │   ├── model/                   (Journey, CheckIn, Milestone, StreakStats,
│       │   │   │                             BookPassage, DailyVerse, JourneyCategory, Enums)
│       │   │   ├── data/                    (StreakCalculator, SharedStreakStorage,
│       │   │   │                             EmergencyContactStorage, AppSettingsStorage,
│       │   │   │                             HeritageStore, PassageStore)
│       │   │   ├── widget/                  (ClearStreakWidget, ClearStreakWidgetReceiver)
│       │   │   ├── navigation/              (Screen)
│       │   │   └── ui/
│       │   │       ├── theme/               (Color, Theme, Type, Shape, Spacing)
│       │   │       ├── components/          (UrgePulseGrid, HaltTriggerRow, BreathingCircle,
│       │   │       │                         GroundingTimer, BoxBreather, PocketAnchor,
│       │   │       │                         MiniGamesCard, CalendarHeatmap, VerseCalendar,
│       │   │       │                         OIAButton, OIACard, OIATextField)
│       │   │       └── screens/             (Dashboard, CheckInModal, CrisisIntercept,
│       │   │                                 JourneyDetail, Journal, Settings, AddJourney,
│       │   │                                 BiometricLock, GroundingTools, Heritage [Reflect],
│       │   │                                 Science, Unlock)
│       │   └── res/                         (values, xml, drawable, mipmap, layout)
│       ├── core/java/.../billing/           (CorePremiumManager — always-unlocked stub)
│       └── store/
│           ├── AndroidManifest.xml          (adds INTERNET + BILLING)
│           └── java/.../billing/            (StoreBillingManager — Play Billing)
├── .github/workflows/build.yml
├── overview.html
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

*Classification: 864zeros Internal — In active development*

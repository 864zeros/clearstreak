# ClearStreak

**864zeros LLC Local-First Utility | Pillar: Faith, Health & Growth**  
*Privacy-First Sobriety & Recovery Companion for Android*

> **"Your recovery is yours alone. We cannot see it, sell it, subpoena it, or lose it."**

---

## 🌟 Overview

ClearStreak counters the surveillance economics of incumbent recovery apps by operating entirely on-device: **no accounts, no cloud sync, no telemetry, and no subscriptions**. Recovery data is encrypted with hardware-bound keys via Android Keystore / StrongBox and never leaves the device.

Built strictly according to the **864zeros Build Kit** and the **OIA Design System v1.0**.

---

## 🛡️ Core Security Architecture

1. **Air-Gapped by Default**: `android.permission.INTERNET` is completely removed from the default `core` build flavor.
2. **Hardware-Bound Biometric Gate**: `BiometricPrompt` with `CryptoObject` gates all access to the encrypted database (`recovery_enc.db`). Master key generated with `setUserAuthenticationRequired(true)` and `setInvalidatedByBiometricEnrollment(true)`.
3. **Database Partitioning**:
   - `streak_core.db`: Plaintext SQLite for journey records, milestone tracking, and Glance widget synchronization.
   - `recovery_enc.db`: AES-256 encrypted with SQLCipher 4.x for check-in records, reflection notes, and sensitive journal entries.
   - `coping_cards.db`: Curated table with 150+ deterministic CBT coping cards.
4. **Zero Cloud Backup**: Excluded via `android:allowBackup="false"` and `<data-extraction-rules>`.
5. **Screen Protection**: `FLAG_SECURE` prevents OS-level screen captures and task switcher snapshot leaks.

---

## 🎨 OIA Design System v1.0 Implementation

- **Palette**: Warm Neutrals (Cream `#F5F2ED`, Warm White `#FDFCFA`, Dark BG `#1A1A1A`, Dark Card `#242424`) with Sage (`#8BA888`), Coral (`#E8A598`), Mustard (`#C9A86C`), and Dusty Blue (`#7A8FA3`). **No pure `#000000` or `#FFFFFF`**.
- **Typography**: Nunito type scale with rounded terminals and relaxed line heights.
- **ADHD-Friendly UX**:
  - One primary action per screen.
  - Non-shaming relapse model: slips are logged as data points, preserving historical records and cumulative days clear.
  - Large touch targets (56dp).
  - 60-second somatic grounding timer & 4-7-8 rhythmic breathing circle with haptic feedback.
  - Crisis Intercept screen with native `tel://` dialers (Sponsor, Support Person, SAMHSA `1-800-662-4357`, 988 Lifeline), anonymous map queries (`geo:0,0?q=AA+meeting+near+me`), and 2-second long-press exit confirmation.

---

## 📱 Jetpack Glance Home Screen Widget

- Reads streak counts and milestones directly from sandboxed `SharedStreakStorage`.
- One-tap intent launches the app directly into the Check-In modal.

---

## 🏗️ Build Flavors

- `coreDebug` / `coreRelease`: Completely air-gapped zero-network build.
- `storeDebug` / `storeRelease`: Minimal networking strictly for optional Google Play Billing receipt validation.

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
│       │   ├── java/com/_864zeros/clearstreak/
│       │   │   ├── ClearStreakApp.kt
│       │   │   ├── MainActivity.kt
│       │   │   ├── security/
│       │   │   │   ├── KeyStoreManager.kt
│       │   │   │   └── DatabasePassphraseProvider.kt
│       │   │   ├── database/
│       │   │   │   ├── DatabaseManager.kt
│       │   │   │   └── CopingCardsSeed.kt
│       │   │   ├── model/
│       │   │   │   ├── Enums.kt
│       │   │   │   ├── Journey.kt
│       │   │   │   ├── CheckIn.kt
│       │   │   │   ├── CopingCard.kt
│       │   │   │   └── StreakStats.kt
│       │   │   ├── data/
│       │   │   │   ├── StreakCalculator.kt
│       │   │   │   ├── SharedStreakStorage.kt
│       │   │   │   └── EmergencyContactStorage.kt
│       │   │   ├── widget/
│       │   │   │   ├── ClearStreakWidget.kt
│       │   │   │   └── ClearStreakWidgetReceiver.kt
│       │   │   └── ui/
│       │   │       ├── theme/
│       │   │       ├── components/
│       │   │       ├── screens/
│       │   │       └── navigation/
│       │   └── res/
│       │       ├── values/
│       │       └── xml/
│       └── store/
│           └── AndroidManifest.xml
├── content/
│   └── coping_cards_v1.csv
├── scripts/
│   └── build_coping_cards.py
├── ClearStreak_Spec_v2.md
├── gemini.md
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
    └── libs.versions.toml
```

---

*Classification: 864zeros Internal — Ready for Build*

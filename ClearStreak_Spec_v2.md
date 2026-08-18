# ClearStreak Product Specification
## 864zeros Local-First Utility | Pillar: Faith, Health & Growth

---

## 1. Executive Overview

ClearStreak is a single-purpose, privacy-first sobriety and recovery companion built on the 864zeros local-first, zero-knowledge utility framework. It counters the surveillance economics of incumbent recovery apps by operating entirely on-device: no accounts, no cloud sync, no telemetry, and no subscription. Recovery data is encrypted with hardware-bound keys and never leaves the device.

**Core Promise:** *Your recovery is yours alone. We cannot see it, sell it, subpoena it, or lose it.*

---

## 2. Naming & Trademark

| Status | Name | Platform | Conflict Risk |
|---|---|---|---|
| ❌ Rejected | ClearDay | iOS / Android | Active apps by LiveRehab and EverAfter Games |
| ✅ Approved | **ClearStreak** | iOS / Android | No active conflicts |
| 🔄 Reserve | TrueZero | iOS / Android | Backup name |
| 🔄 Reserve | DayClear | iOS / Android | Backup name |

**Product Name:** ClearStreak  
**Bundle ID:** `com.864zeros.clearstreak`  
**Category:** Health & Fitness > Medical

---

## 3. Problem Statement

The digital recovery market is dominated by apps that exploit vulnerability:

- **I Am Sober** ($39.99/year): Cloud-backed streak data, aggressive subscription gates, relapse-shaming UI that wipes history on reset.
- **Monument**: Settled with the FTC for sharing user recovery data with third-party advertisers without consent.
- **General market pattern**: Recovery apps require account creation, harvest behavioral data, and monetize through recurring subscriptions that create friction during moments of crisis.

**The Gap:** There is no recovery tool that treats user privacy as a *safety feature* rather than a compliance checkbox. Users in recovery — particularly those in regulated professions, custody disputes, or stigmatized environments — need a tool that cannot be subpoenaed, breached, or inspected.

---

## 4. Product Philosophy

### 4.1 Data Over Shame
A relapse is a data point, not a moral failure. ClearStreak preserves all history through slips: longest streaks, cumulative days clear, and trigger patterns remain intact. The streak counter resets, but the journey record grows.

### 4.2 Air-Gapped by Default
No network permission is required for core functionality. The app operates without an internet connection. Optional, explicitly consented networking is available only for App Store in-app purchase validation and anonymized crash telemetry — both with clear opt-in disclosures.

### 4.3 Crisis-First Design
Every interaction is optimized for speed during vulnerability. The path from app launch to logged urge or activated rescue tool is under 3 seconds.

### 4.4 Hardware-Bound Security
Encryption keys are tied to device biometrics via Secure Enclave (iOS) and StrongBox/TEE (Android). Data cannot be decrypted without the user's physical presence.

---

## 5. v1 MVP Scope

### P0 — Ship Blockers (Weeks 1–6)
| Feature | Description |
|---|---|
| **Multi-Journey Streak Tracker** | Track concurrent recoveries (alcohol, smoking, gambling, substances, behavioral, custom) with independent start dates and counters. |
| **4-Tier Urge Pulse** | One-tap classification: 🟢 Clear, 🟡 Passing Thought, 🟠 White-Knuckling, 🔴 Critical. |
| **HALT Trigger Row** | One-tap context: 🍏 Hungry, 😡 Angry, 👤 Lonely, 🥱 Tired, ⚡ Stressed, 😞 Hopeless. |
| **Unified Crisis Intercept** | Single fullscreen rescue screen for 🔴 Critical state: sponsor dialer, support contacts, national helplines (SAMHSA, 988), meeting finder deep-links, 60-second grounding timer, 4-7-8 breathing. |
| **Encrypted Recovery Journal** | SQLCipher-encrypted (AES-256) journal entries with biometric unlock. |
| **Deterministic Coping Cards** | Pre-authored, trigger-matched coping strategies displayed based on urge level + HALT input. No ML in v1. |
| **Home Screen Widgets** | iOS WidgetKit + Android Glance: live streak counter, next milestone countdown, one-tap urge log. |
| **Biometric Gate** | Face ID / Touch ID / Fingerprint required for app entry and journal access. |
| **One-Time Purchase** | $4.99 lifetime unlock via App Store / Play Store IAP. |

### P1 — Post-Launch (Weeks 7–12)
| Feature | Description |
|---|---|
| **Pattern Insights Dashboard** | Local analytics: urge frequency by time-of-day, day-of-week, HALT correlation, slip trigger clustering. |
| **Custom Coping Card Builder** | User-authored cards with favorites and quick-access pinning. |
| **Export / Backup** | Encrypted local backup to device Files app (user-initiated, air-gapped). |
| **Supporter Tier** | $9.99 voluntary "pay-it-forward" unlock — identical features, funds development. |

### P2 — Future Exploration (v2+)
| Feature | Description |
|---|---|
| **On-Device CBT Assistant** | Lightweight edge model (sub-200MB) for urge deconstruction and personalized coping synthesis. Deferred until on-device inference matures and binary size impact is justified by user demand. |
| **Literature Companion** | Original licensed or public-domain recovery texts with FTS5 search. **Blocked until original content pipeline or licensed partnership is secured.** |
| **Wearable Integration** | Apple Watch / Wear OS complication for one-tap urge logging and haptic grounding. |

---

## 6. Detailed Feature Specification

### 6.1 Streak Tracking Engine

**Journey Model:**
- Each recovery target is a "Journey" with independent metadata, start timestamp, and daily cost savings.
- Journeys can be archived, not deleted, preserving historical data.
- Midnight rollover is handled by native system alarms; widgets refresh via shared sandbox storage.

**Relapse Handling:**
- User marks a slip. The active streak resets to zero.
- The slip is recorded as a `check_in` row with `is_slip = 1`, preserving the timestamp, urge level, HALT trigger, and journal note.
- Longest streak and cumulative days clear are calculated across the full history and remain visible.
- The UI language never uses "failure," "broken," or "lost." Copy uses "reset," "new start," and "slip logged."

### 6.2 4-Tier Urge Pulse + HALT

**Interaction Flow:**
1. User opens app (biometric gate).
2. Primary screen displays active journeys with current streaks.
3. Floating action button: "Check In."
4. Modal presents 4-tier grid:
   - 🟢 **Clear** → Instant reinforcement banner; streak counter increments.
   - 🟡 **Passing Thought** → HALT row appears; user selects trigger; brief grounding phrase displayed; optional journal note.
   - 🟠 **White-Knuckling** → HALT row + auto-launches 60-second grounding timer (box breathing or 5-4-3-2-1 somatic reset); optional journal note; deterministic coping card displayed.
   - 🔴 **Critical** → App transitions to **Unified Crisis Intercept** fullscreen; all navigation locked until user confirms "I am safe" or 60 seconds elapse.

### 6.3 Unified Crisis Intercept (Red Rescue Hub)

**Layout:** High-contrast dark mode, large touch targets, no scroll required for primary actions.

```
┌─────────────────────────────────────────────┐
│  🛑 STOP. TAKE ONE BREATH.                  │
│     YOU DO NOT HAVE TO ACT.                 │
│                                             │
│  [ 📞 CALL SPONSOR ]      (tel://)          │
│  [ 👥 CALL SUPPORT PERSON ] (tel://)        │
│                                             │
│  [ 📍 FIND A MEETING NOW ]  (maps intent)   │
│                                             │
│  [ 🆘 SAMHSA 1-800-662-4357 ]  (tel://)    │
│  [ 🆘 988 LIFELINE ]           (tel://)    │
│                                             │
│  ─────────────────────────────────────      │
│                                             │
│  [ 🫁 60-SEC GROUNDING TIMER ]              │
│  [ 🌬️ 4-7-8 BREATHING CIRCLE ]             │
│                                             │
│  [ ✅ I AM SAFE — RETURN TO APP ]           │
└─────────────────────────────────────────────┘
```

**Requirements:**
- All tel:// links launch native dialer without network dependency.
- Map deep-links use geo queries (e.g., `geo:0,0?q=AA+meeting+near+me`) without logging location to the app.
- Haptic pacing during breathing exercises (no audio required, works in silent mode).
- "I am safe" button requires long-press (2 seconds) to prevent accidental dismissal.

### 6.4 Deterministic Coping Card System (v1)

Instead of on-device LLM, v1 uses a curated SQLite table of coping strategies matched by urge level and HALT trigger.

**Matching Logic:**
```
SELECT action_text, rationale 
FROM coping_cards 
WHERE trigger_category = :halt_trigger 
  AND min_urge_level <= :urge_level 
ORDER BY is_favorite DESC, RANDOM() 
LIMIT 1;
```

**Card Structure:**
- `action_text`: 1–2 sentences, imperative, physical action (e.g., "Drink 16oz of cold water. Walk to the nearest window and name 3 things you see.")
- `rationale`: 1 sentence explaining why this works (CBT grounding principle).
- `trigger_category`: HALT value or `GENERAL`.
- `min_urge_level`: CLEAR, PASSING, WHITE_KNUCKLING, CRITICAL.

**Content Source:** Original content authored by licensed recovery counselors. 150+ cards at launch covering all HALT × Urge combinations.

### 6.5 Widget Strategy

**iOS WidgetKit:**
- **Small:** Current streak count for primary journey + "Check In" button (opens app to check-in modal).
- **Medium:** Streak count + next milestone (e.g., "30 days in 2 days") + 1-tap urge log (🟢🟡🟠🔴) via App Intent.
- **Lock Screen:** Live Activity during active grounding timer.

**Android Glance:**
- **1×1:** Streak counter.
- **3×1:** Streak + next milestone + quick urge log.
- **Tile (Wear OS):** One-tap "I'm struggling" that opens app directly to Crisis Intercept.

**Data Flow:**
Widgets read from shared App Group (iOS) or `SharedPreferences` backed by sandboxed storage (Android). No direct database access from widget process.

---

## 7. Technical Architecture

### 7.1 High-Level Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                        USER MOBILE DEVICE                           │
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    APP LIFECYCLE ENGINE                      │   │
│  │         [SwiftUI / Jetpack Compose + Widgets]               │   │
│  └───────┬────────────────────┬───────────────────────┬────────┘   │
│          │                    │                       │              │
│          ▼                    ▼                       ▼              │
│  ┌──────────────┐    ┌─────────────────┐    ┌─────────────────┐   │
│  │ streak_core  │    │ recovery_enc    │    │ coping_cards    │   │
│  │ SQLite       │    │ SQLCipher       │    │ SQLite          │   │
│  │ (plaintext)  │    │ AES-256-GCM     │    │ (bundled)       │   │
│  │ Widget sync  │    │ Biometric key   │    │ 150+ rows       │   │
│  └──────────────┘    └─────────────────┘    └─────────────────┘   │
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  SECURITY LAYER                                             │   │
│  │  • LocalAuthentication (iOS) / BiometricPrompt (Android)   │   │
│  │  • Secure Enclave / StrongBox key derivation               │   │
│  │  • Key invalidated on biometric enrollment change          │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
│  [ CORE: ZERO NETWORK ]  [ OPTIONAL: IAP + Crashlytics (opt-in) ]  │
└─────────────────────────────────────────────────────────────────────┘
```

### 7.2 Database Schema

#### journeys
```sql
CREATE TABLE journeys (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    category TEXT NOT NULL CHECK(category IN ('substance','behavioral','custom')),
    start_timestamp INTEGER NOT NULL,
    daily_cost_savings REAL DEFAULT 0.0,
    is_archived INTEGER DEFAULT 0,
    created_at INTEGER DEFAULT (strftime('%s','now'))
);
```

#### check_ins
```sql
CREATE TABLE check_ins (
    id TEXT PRIMARY KEY,
    journey_id TEXT NOT NULL,
    timestamp INTEGER NOT NULL,
    mood_score INTEGER CHECK(mood_score BETWEEN 1 AND 5),
    urge_level TEXT NOT NULL CHECK(urge_level IN ('CLEAR','PASSING','WHITE_KNUCKLING','CRITICAL')),
    halt_trigger TEXT CHECK(halt_trigger IN ('HUNGRY','ANGRY','LONELY','TIRED','STRESSED','HOPELESS')),
    note_encrypted BLOB,
    is_slip INTEGER DEFAULT 0,
    is_crisis_intercept INTEGER DEFAULT 0,
    coping_card_id TEXT,
    FOREIGN KEY(journey_id) REFERENCES journeys(id) ON DELETE CASCADE
);
```

#### coping_cards
```sql
CREATE TABLE coping_cards (
    id TEXT PRIMARY KEY,
    trigger_category TEXT NOT NULL,
    min_urge_level TEXT NOT NULL CHECK(min_urge_level IN ('CLEAR','PASSING','WHITE_KNUCKLING','CRITICAL')),
    action_text TEXT NOT NULL,
    rationale TEXT,
    is_favorite INTEGER DEFAULT 0,
    is_user_created INTEGER DEFAULT 0
);
```

### 7.3 Security Specification

**Encryption:**
- SQLCipher 4.x with AES-256-GCM page encryption.
- Encryption key derived from hardware keystore, not user password.
- iOS: `SecAccessControlCreateFlags` with `.biometryCurrentSet` — key invalidated if new fingerprints/face added.
- Android: `KeyGenParameterSpec` with `setUserAuthenticationRequired(true)` and `setInvalidatedByBiometricEnrollment(true)`.

**Data Protection:**
- `recovery_enc.db`: Excluded from iCloud Backup (`NSURLIsExcludedFromBackupKey`) and Android Auto-Backup (`android:allowBackup="false"`).
- `streak_core.db`: May be backed up (contains no sensitive journal content).
- Screenshots disabled on journal and crisis intercept screens (iOS `UIApplication.shared.isIdleTimerDisabled` + `secureTextEntry` overlay; Android `FLAG_SECURE`).

**Network Policy:**
- Android: `android.permission.INTERNET` **removed** from manifest for core build.
- Separate "Release" flavor includes minimal networking for IAP receipt validation and Crashlytics (opt-in, anonymized, no PII).
- iOS: No network entitlements required; StoreKit 2 for IAP operates through Apple frameworks.

---

## 8. Monetization & Financial Model

### 8.1 Pricing
| Tier | Price | Features |
|---|---|---|
| **Core Unlock** | $4.99 one-time | Full app access, unlimited journeys, encrypted journal, widgets, crisis tools |
| **Supporter** | $9.99 one-time | Identical features; voluntary premium that funds development and content expansion |

### 8.2 Revenue Projection

**Conservative Scenario (Year 1):**
- Addressable: Recovery app market with ~2M monthly active users; ~350k paid subscribers across incumbents.
- Conversion assumption: 0.5% of addressable free users + 2% of incumbent paid users churning to privacy-first alternative.
- **Target:** 8,000–12,000 units in Year 1.
- **Gross Revenue:** $40,000–$60,000 (before 15–30% platform fees).
- **Net Revenue:** $28,000–$51,000.

**Strategic Positioning:**
ClearStreak is not designed as a standalone venture-scale business. It is a **trust-product** within the 864zeros portfolio:
- Proves the local-first architecture for future FHG tools.
- Generates brand equity in the privacy-respecting software space.
- Creates a user base for future companion products (meditation, habit tracking, faith-based tools).

---

## 9. Content Strategy

### 9.1 Original Content Pipeline
The v1 coping card library and future literature companion will use **100% original or properly licensed content**.

**Phase 1 (v1):** Hire 1–2 licensed counselors (LCSW, CADC, or peer recovery specialists) to author:
- 150 deterministic coping cards
- 30 grounding scripts for the breathing timer
- In-app copy and crisis screen language

**Phase 2 (v2+):** Partner with recovery nonprofits or independent authors to license contemporary recovery literature for on-device FTS5 search.

**Explicitly Excluded:** Any content from Alcoholics Anonymous World Services, Narcotics Anonymous World Services, or other copyrighted 12-step literature without signed license agreement.

### 9.2 Content Database Build
```python
# build_coping_cards.py — Original Content Pipeline
import sqlite3
import csv
import uuid

DB_NAME = "coping_cards.db"

def init_db():
    conn = sqlite3.connect(DB_NAME)
    c = conn.cursor()
    c.execute('''
        CREATE TABLE coping_cards (
            id TEXT PRIMARY KEY,
            trigger_category TEXT NOT NULL,
            min_urge_level TEXT NOT NULL,
            action_text TEXT NOT NULL,
            rationale TEXT,
            is_favorite INTEGER DEFAULT 0,
            is_user_created INTEGER DEFAULT 0
        )
    ''')
    conn.commit()
    return conn

def ingest_from_csv(conn, csv_path):
    # CSV columns: trigger_category, min_urge_level, action_text, rationale
    with open(csv_path, 'r') as f:
        reader = csv.DictReader(f)
        c = conn.cursor()
        for row in reader:
            c.execute('''
                INSERT INTO coping_cards (id, trigger_category, min_urge_level, action_text, rationale)
                VALUES (?, ?, ?, ?, ?)
            ''', (str(uuid.uuid4()), row['trigger_category'], row['min_urge_level'],
                  row['action_text'], row.get('rationale', '')))
    conn.commit()

if __name__ == "__main__":
    conn = init_db()
    # ingest_from_csv(conn, "content/coping_cards_v1.csv")
    conn.close()
```

---

## 10. Build Commands

### 10.1 Android (Kotlin + Jetpack Compose)

```bash
gemini "Generate a local-first Android Kotlin project for ClearStreak with the following constraints:
1. AndroidManifest.xml: exclude android.permission.INTERNET from default flavor. Add a 'release' flavor with minimal networking for IAP.
2. Dependencies: SQLCipher (net.zetetic:android-database-sqlcipher), BiometricPrompt (androidx.biometric:biometric).
3. MainActivity: BiometricPrompt gate before any database access. Use CryptoObject for key derivation.
4. UI: 4-tier Urge Grid (Clear, Passing, White-Knuckling, Critical) with HALT trigger row.
5. Crisis Intercept: Fullscreen Red Rescue Hub with tel:// dialers, map intents, 60-sec grounding timer, and 4-7-8 breathing circle animation.
6. Database: SQLCipher with StrongBox-backed key. Exclude from backup.
7. Widget: Jetpack Glance widget reading streak data from shared sandbox storage."
```

### 10.2 iOS (Swift + SwiftUI + WidgetKit)

```bash
claude "Create a native iOS SwiftUI project for ClearStreak with the following constraints:
1. Add GRDB.swift with SQLCipher for AES-256 encrypted database operations.
2. Biometric lock: LocalAuthentication with .biometryCurrentSet flag; key stored in Secure Enclave.
3. WidgetKit extension: Small and medium widgets reading from shared App Group container. Include App Intents for one-tap urge logging.
4. UI: 4-tier check-in screen with HALT trigger icons. Critical state triggers fullscreen Crisis Intercept.
5. Crisis Intercept: tel:// sponsor dialer, 988/SAMHSA dialers, map intent for meetings, 60-second grounding timer with haptic feedback.
6. Database: Exclude from iCloud backup via NSURLIsExcludedFromBackupKey.
7. StoreKit 2: One-time $4.99 IAP with no subscription."
```

---

## 11. Success Metrics (v1)

| Metric | Target | Measurement |
|---|---|---|
| App Store Rating | ≥ 4.6 | Native review prompt after 7-day streak |
| Day-1 Retention | ≥ 40% | Local analytics, anonymized |
| Crisis Intercept Usage | ≥ 1% of active users / week | Local event log |
| Check-in Frequency | ≥ 3x / week / active user | Local analytics |
| Journal Entry Rate | ≥ 20% of check-ins include note | Local analytics |
| Refund Rate | ≤ 5% | App Store Connect / Play Console |

---

## 12. Risk Register

| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| App Store rejection for medical claims | Medium | High | Frame as "habit tracker," avoid diagnosing language. Include disclaimer: "Not a substitute for professional treatment." |
| Biometric key loss (user changes biometrics) | Medium | High | Clear onboarding warning; offer encrypted backup export before biometric changes. |
| Low discoverability in crowded health category | High | Medium | Target "privacy" and "offline" keywords; leverage 864zeros brand; PR push on privacy blogs. |
| Content liability (crisis advice) | Low | High | All coping cards reviewed by licensed counselor; crisis screen includes disclaimer and helpline numbers only. |
| Platform fee erosion | Certain | Medium | Price at $4.99 to absorb 30% fee; Supporter tier at $9.99 improves margin. |

---

## 13. Appendix: Revised Priority Matrix

| Quarter | Deliverable |
|---|---|
| **Q1** | v1 MVP: Streaks, 4-tier pulse, HALT, Crisis Intercept, encrypted journal, deterministic coping cards, widgets, biometric lock, $4.99 IAP |
| **Q2** | Pattern dashboard, custom coping cards, encrypted export, Supporter tier, ASO optimization |
| **Q3** | On-device CBT assistant (pilot with beta users), Wear OS / Apple Watch complication |
| **Q4** | Original content expansion, nonprofit partnership for licensed literature, internationalization |

---

*Document Version: 2.0*  
*Last Updated: 2026-08-18*  
*Classification: 864zeros Internal — Ready for Build*

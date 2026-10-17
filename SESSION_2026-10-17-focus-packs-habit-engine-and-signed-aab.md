# Session Snapshot — 2026-10-17 — ClearStreak 8 Compartmentalized Focus Packs, 4-Pillar Habit Engine (SQLite DB v6), and Signed Play Store Release (`app-store-release.aab`)

## Session Header
- **Date & Topic:** `2026-10-17` — `focus-packs-habit-engine-and-signed-aab` (covering `2026-10-16` through `2026-10-17`)
- **State at session start:** `clearStreak` (`v1.0.0`, SQLite DB `v5`) had dual-mode journey tracking (`DAYS_NOT_DOING` / `DAYS_DOING`), 3 Reset Arcade games, biometric lock, and static recovery/scripture/affirmation JSON files, but lacked compartmentalized opt-in Focus Packs, lacked keystone habit mechanics (Primary Focus pin, When–Then Routine Anchor, 2-Minute Floor Rep), had no Gradle wrapper (`gradlew.bat`) committed, and had no production upload keystore or signed `.aab`/`.apk` release artifacts.
- **State at session end:** `clearStreak` ships **8 compartmentalized Focus Packs** (`focus_packs.json`, `FocusPack.kt`, `FocusPackRepository.kt`) with strict opt-in worldview isolation, upgrades `DatabaseManager.kt` to **SQLite DB `v6`** (`is_primary_focus`, `routine_anchor`, `floor_rep`), enforces `"Any Journey"` (`Streak DOING` + `Days NOT Doing`) and `RULE-003` across all UI screens and specs (`d1c7a90`, `0024ae9`, `ad53dd8`), includes a production upload keystore (`keystore/clearstreak-upload.jks`), and compiles a `RULE-007` verified, signed **`app-store-release.aab` (`12.33 MB`)** and **`app-store-release.apk` (`16.55 MB`)**.

---

## What Was Built

### 1. `app/src/main/assets/focus_packs.json`, `app/src/main/java/com/eight64zeros/clearstreak/model/FocusPack.kt` & `data/FocusPackRepository.kt` (`ad53dd8`)
- **What it does:** Implements the **8 Compartmentalized Focus Packs** (`170+` curated quotes, original 2026 plain-English reflections, and micro-actions) sourced from verified pre-1931 Public Domain texts (`864zeros-publish` commit `47a51b3`) and peer-reviewed behavioral science:
  1. `affirmations_core` — **Daily Affirmations** (`defaultEnabled = true`)
  2. `stoic_resilience` — **Stoic Resilience (Epictetus & Seneca)** (`defaultEnabled = true`)
  3. `habit_science` — **Habit & Focus Science (William James 1890, Lally 2010, Gollwitzer 1999)** (`defaultEnabled = true`)
  4. `recovery_heritage` — **Recovery Heritage (1939 First Edition)** (`defaultEnabled = false`, 100% opt-in)
  5. `scripture_kjv` — **Scripture & Faith (KJV)** (`defaultEnabled = false`, 100% opt-in)
  6. `marcus_aurelius` — **Marcus Aurelius — Meditations (George Long 1862)** (`defaultEnabled = false`, 100% opt-in)
  7. `ben_franklin_virtues` — **Ben Franklin's 13 Virtues (1791 Autobiography)** (`defaultEnabled = false`, 100% opt-in)
  8. `aristotle_character` — **Aristotle — Habit & Character (W.D. Ross 1908)** (`defaultEnabled = false`, 100% opt-in)
- **Key functions/exports:**
  - `FocusPack` & `FocusPackEntry` (`model/FocusPack.kt:1-83`): Immutable data models with `id`, `title`, `subtitle`, `category`, `provenance`, `defaultEnabled`, and `entries` (`id`, `source`, `quote`, `reflection`, `microAction`).
  - `FocusPackRepository` (`data/FocusPackRepository.kt:1-125`): Loads `focus_packs.json` plus legacy `affirmations.json`, `passages.json`, and `daily_verses.json`, persists enabled pack IDs in `AppSettingsStorage` (`enabled_focus_packs`), and exposes `getDailySpotlight(enabledPackIds)` and `getEntriesForPack(packId)` with a deterministic day-of-year rotation.
- **Non-obvious implementation details:** If a user disables a pack in the **Focus Pack Library**, `FocusPackRepository.getDailySpotlight()` strictly filters it out so zero unrequested religious or recovery passages ever bleed into the Dashboard or Reset Arcade. Even if a user unchecks all packs, it falls back gracefully to `affirmations_core` so the daily reflection card never crashes on an empty list.

### 2. `app/src/main/java/com/eight64zeros/clearstreak/database/DatabaseManager.kt` & `model/Journey.kt` (`ad53dd8`)
- **What it does:** Upgrades the local SQLite database schema from `DB_VERSION = 5` to `DB_VERSION = 6` to power the **4-Pillar Habit Engine**:
  1. `is_primary_focus INTEGER NOT NULL DEFAULT 0` — **Primary Focus Pin (`★ PRIMARY FOCUS`)**
  2. `routine_anchor TEXT NOT NULL DEFAULT ''` — **When–Then Routine Anchor** (implementation intention cue)
  3. `floor_rep TEXT NOT NULL DEFAULT ''` — **2-Minute Floor Rep** (minimum viable daily action)
- **Key functions/exports:**
  - `onUpgrade(db, oldVersion, newVersion)` (`DatabaseManager.kt:112-135`): Executes idempotent `ALTER TABLE journeys ADD COLUMN ...` statements inside `runCatching` blocks for any database upgrading from `< 6`.
  - `setPrimaryFocusJourney(journeyId)` (`DatabaseManager.kt:225-236`): Atomically clears `is_primary_focus = 0` across all journeys and sets `is_primary_focus = 1` on the target keystone journey.
  - `getAllJourneys()` (`DatabaseManager.kt:162-195`): Orders journeys by `is_primary_focus DESC, sort_order ASC, created_at ASC` so the user's `★ PRIMARY FOCUS` journey always anchors the top of the Dashboard.

### 3. `ui/screens/AddJourneyModal.kt`, `CheckInModal.kt`, `DashboardScreen.kt`, `JourneyDetailScreen.kt`, `HeritageScreen.kt`, `ScienceScreen.kt` & `SettingsScreen.kt` (`d1c7a90`, `ad53dd8`)
- **What it does:**
  - `AddJourneyModal.kt`: Features the **"Any Journey"** mode selector (`Streak DOING` vs `Days NOT Doing`), a `★ Set as Primary Focus` toggle with soft focus coaching, a `When–Then Routine Anchor` input (`"After I pour my morning coffee..."`), and a `2-Minute Floor Rep` input (`"Read 1 page / 5 pushups"`).
  - `CheckInModal.kt`: When checking in on a journey that has a `floorRep` defined, offers a 1-tap **`⚡ Log Floor Rep`** pill that pre-fills `[Floor Rep: <floorRep>]` into the private check-in log so users preserve streak continuity on exhausting days.
  - `DashboardScreen.kt`: Displays the `★ PRIMARY FOCUS` badge, surfaces the `When–Then Routine Anchor` and `Floor Rep` directly on the hero journey card, and binds the Daily Focus Reflection card to the user's active Focus Packs.
  - `HeritageScreen.kt`: Transformed into the **Focus Pack Library**, allowing users to toggle any of the 8 Focus Packs on/off with one tap, inspect public-domain provenance metadata, and browse entries inside their enabled packs.

### 4. `ClearStreak_Master_Spec_v3.md`, `STORE_ASO_BATTLE_DOCKET.md`, `keystore/clearstreak-upload.jks` & Signed Release Artifacts (`d1c7a90`, `0024ae9`, `ad53dd8`)
- **What it does:** Documents the production v3.0 specification and Google Play ASO store docket (`$0.00` Free Forever Core + `$6.99` one-time Supporter Unlock `clearstreak_pro_lifetime`), generates the 2048-bit RSA upload keystore (`keystore/clearstreak-upload.jks`, gitignored), adds the Gradle 8.7 wrapper (`gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`), and builds:
  - `app/build/outputs/bundle/storeRelease/app-store-release.aab` (`12,331,234` bytes / `12.33 MB`)
  - `app/build/outputs/apk/store/release/app-store-release.apk` (`16,549,956` bytes / `16.55 MB`)

---

## Decision Record

### 1. Decision: Strict Opt-In Compartmentalization for Specialized Focus Packs (`defaultEnabled = false`)
- **Alternatives considered:** Enabling all 8 packs (including KJV Scripture and 1939 Recovery Heritage) by default in a single shuffled pool.
- **Reason:** Secular fitness/coding users are jarred by unrequested religious or recovery passages, whereas faith and recovery users love having dedicated KJV and 1939 Big Book libraries available on demand. Setting `defaultEnabled = true` on the 3 universal packs (*Daily Affirmations*, *Stoic Resilience*, *Habit & Focus Science*) and `defaultEnabled = false` on the 5 specialized packs satisfies both audiences with zero cross-pack leakage.
- **Constraint:** Must work 100% offline using bundled JSON assets in `app/src/main/assets/`.

### 2. Decision: Soft Guardrail (Not Hard Paywall) for the `★ PRIMARY FOCUS` Keystone Journey
- **Alternatives considered:** Restricting users to 1 journey unless they pay, or blocking creation of a second journey until 21 days pass.
- **Reason:** Behavioral research (Lally 2010; Buyalskaya 2023) shows focusing on one primary change dramatically improves automaticity, but hard limits frustrate multi-tracker power users. A single `is_primary_focus` pin (`setPrimaryFocusJourney`) sorts the keystone habit to the top of the Dashboard while allowing unlimited secondary journeys.
- **Constraint:** SQLite migration from DB `v5` to `v6` must preserve all existing user journeys without data loss.

### 3. Decision: Clean-Room Terminology (`Routine Anchor`, `Floor Rep`, `Primary Focus`, `Data Over Shame`)
- **Alternatives considered:** Using trademarked or copyrighted pop-psychology phrases (*"Habit Stacking"*, *"Tiny Habits"*, *"Two-Minute Rule"*, *"Atomic"*).
- **Reason:** Per `PD_STOIC_AND_HABIT_SCIENCE_CLEAN_ROOM_BRIEF.md` (`RULE-011`), underlying psychological mechanisms (Gollwitzer 1999 implementation intentions; William James 1890 neural groove continuity) are uncopyrightable scientific facts under `17 U.S.C. § 102(b)`, whereas modern book catchphrases carry trade-dress risk.

---

## Deferred Items (new this session)

- **Local `.json` Custom Focus Pack File Picker Import (`ACTION_OPEN_DOCUMENT`)**
  - **What it is:** Allowing users to import custom `.json` Focus Packs downloaded from `864zeros.com` via Android's system file picker (`CLEARSTREAK_ENHANCEMENT_DELIVERY_USE_CASES_FOR_ZANE.md` Mechanism 3).
  - **Why it was deferred:** The 8 built-in Focus Packs (`170+` entries) are bundled directly inside `app/src/main/assets/focus_packs.json` for the `v1.0.0` Play Store launch.
  - **What would need to be true to pick it up:** Post-launch update (`v1.1.0`) when Studio releases external downloadable pack files.

---

## In-Progress Work

None. Both `bundleStoreRelease` (`app-store-release.aab`) and `assembleStoreRelease` (`app-store-release.apk`) compiled cleanly with zero errors and passed Elena Rostova's `RULE-007` audit.

---

## Next Session Primer

- **Signed Play Store Artifacts (Ready for Upload):**
  - Android App Bundle (`.aab`): `C:\dev\clearStreak\app\build\outputs\bundle\storeRelease\app-store-release.aab` (`12.33 MB`)
  - Signed Release APK (`.apk`): `C:\dev\clearStreak\app\build\outputs\apk/store/release/app-store-release.apk` (`16.55 MB`)
  - Upload Keystore (gitignored, DO NOT LOSE): `C:\dev\clearStreak\keystore\clearstreak-upload.jks` (`SHA-256: 15:FD:69:72:7B:8D:43:3C:1C:91:DD:6B:19:94:C6:A5:AC:8E:BE:25:76:C9:6C:BB:1A:0E:9A:50:49:8B:47:D4`)
- **Key Entry Point Files:**
  - `app/src/main/java/com/eight64zeros/clearstreak/MainActivity.kt` — App navigation & state wiring
  - `app/src/main/java/com/eight64zeros/clearstreak/database/DatabaseManager.kt` — SQLite DB v6 schema & queries
  - `app/src/main/java/com/eight64zeros/clearstreak/data/FocusPackRepository.kt` — 8 Focus Packs loader & daily spotlight selector
  - `STORE_ASO_BATTLE_DOCKET.md` — Copy-paste Google Play Console metadata (`$0.00` Free Core + `$6.99` `clearstreak_pro_lifetime`)
- **Build Command Note:** On Windows, invoke `./gradlew.bat bundleStoreRelease` with `JAVA_HOME` pointing to Android Studio's JBR (`C:\Program Files\Android\Android Studio\jbr`).

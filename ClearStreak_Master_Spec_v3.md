# ClearStreak — Master Product Specification v3.0 & GTM Voiceover Feature Matrix

- **Product:** ClearStreak (`com.eight64zeros.clearstreak`)
- **Pillar:** Faith, Health & Growth (`FHG`)
- **Tagline:** *The Private Habit, Streak & Journey Companion — Data Over Shame.*
- **Core Doctrine:** *Any Journey. Days Doing or Days Not Doing. 100% On-Device. Zero Cloud. Zero Shame.*
- **Authority Order:** `ClearStreak_Master_Spec_v3.md` (this document) supersedes and unifies `ClearStreak_Blueprint_v1.md`, `PROGRESS.md`, and `ClearStreak_Spec_v2.md`.
- **Last Updated:** 2026-10-16

---

## 1. Executive Summary & The "Any Journey" Dual-Mode Positioning

**ClearStreak** is a sovereign, hardware-encrypted mobile companion built for **any personal journey** — whether a user is **counting a streak of days DOING a positive discipline** or **counting days NOT DOING a habit or vice they are leaving behind**.

Unlike cloud-backed habit trackers and recovery apps that charge $40–$120/year in subscription rent, harvest intimate behavioral logs, and punish a single missed day by wiping the user's entire streak to zero, ClearStreak operates **100% offline on the user's phone** under the **Data Over Shame** ledger:

1. **Mode A — Streak DOING a Journey (Building & Consistency):**
   - Track daily disciplines you are actively showing up for: **Morning Workouts, Prayer & Scripture Study, Deep Work, Cold Plunges, Intermittent Fasting, Reading, Hydration, or 30/75/90-Day Personal Challenges**.
2. **Mode B — Days NOT DOING a Journey (Breaking & Abstaining):**
   - Track habits, compulsions, or vices you are leaving behind: **Late-Night Doomscrolling, Vaping, Alcohol, Sugar, Impulsive Spending, Gaming, or Custom Targets** — complete with real-time **Daily Cost Savings ($)** calculation.

---

## 2. Top 6 Feature Indicators for Voiceovers, Video Scripts & GTM

When authoring voiceovers (Edge-TTS `en-US-AvaNeural`, NotebookLM audio overviews, or live-action/lifestyle shorts), prioritize these **6 Core Value Indicators** in exact order of emotional and conversion impact:

| Priority | Feature Indicator | The Hook / Problem Solved | Exact Voiceover Copy / Soundbite |
| :---: | :--- | :--- | :--- |
| **#1** | **Any Journey: Days Doing OR Days Not Doing** | Most apps force you into either a rigid to-do checklist or a stigmatized addiction label. ClearStreak handles *both* positive streaks and habits you're quitting in one private vault. | *"Whether you're counting a streak of days DOING what builds you up — or days NOT doing what holds you back — ClearStreak tracks every journey on your terms."* |
| **#2** | **Data Over Shame (Permanent History Ledger)** | Incumbent apps wipe your streak to `0` after one off day (triggering the *Abstinence Violation Effect* and making users quit). ClearStreak resets only *today's* active counter while permanently preserving **Lifetime Days**, **Personal Best**, **Ceremonial Milestone Coins**, and **Money Saved**. | *"Miss a day in other apps and they wipe your streak to zero. ClearStreak is built on Data Over Shame: an off day resets today's count, but your lifetime days, personal bests, and milestone coins are never erased."* |
| **#3** | **100% On-Device Hardware Encryption (`SQLCipher AES-256` + `FLAG_SECURE`)** | Intimate habits, slips, and reflections don't belong on a startup's cloud server where they can be leaked, sold, or subpoenaed. ClearStreak has zero servers, zero accounts, and blocks screenshots (`FLAG_SECURE`). | *"No accounts. No cloud servers. Unlocked only by your fingerprint, face, or device PIN with AES-256 encryption. If it isn't on your phone, it doesn't exist."* |
| **#4** | **Eyes-Free Somatosensory Reset (`Pocket Anchor` & `4×4 / 4-7-8 Haptics`)** | When restlessness or an urge hits in public, you don't want to stare at text advice on a bright screen. ClearStreak uses rhythmic haptic pulses you can feel right in your hand or pocket. | *"When a restless moment hits, don't read a wall of text. Trigger the Tactile Pocket Anchor or 4-by-4 haptic box-breather and ride out the wave eyes-free, right in your hand."* |
| **#5** | **Offline Visuospatial Mini-Games & Reflect Vault** | Competing for visuospatial working memory (Elaborated Intrusion theory) quiets craving loops in 3–5 minutes; optional daily wisdom (365 KJV verses + 130 reflection passages + 100 affirmations) grounds the mind offline. | *"Quiet mental noise in three minutes with offline spatial puzzles — or open the optional Reflect vault for daily wisdom and affirmations."* |
| **#6** | **Always-Free Rescue Hub & Anti-SaaS Ownership (`$14.99` Once)** | Stop paying $60/year to count days, and never lock crisis support behind a paywall. ClearStreak gives a 7-day full trial, a single $14.99 lifetime unlock, and keeps the one-tap Rescue Hub 100% free forever. | *"Stop paying subscription rent to build discipline. Seven-day free trial, one-time unlock forever, and a one-tap Rescue Hub that stays 100% free for life."* |

---

## 3. Complete Technical & Feature Architecture (As-Built Inventory)

### 3.1 Multi-Journey Engine (`StreakCalculator` & `streak_core.db`)
- **Dual-Direction Framing:** Users can track unlimited concurrent journeys — both **Days Doing** (habits being built) and **Days Not Doing** (habits/vices being left behind).
- **Custom Labels & Categories:** `Behavioral`, `Custom`, `Vape`, `Alcohol`, `Gambling`, `Drugs`, plus user-defined `customLabel` pills and back-dated start-date picker.
- **Permanent Ledger (`StreakStats`):**
  - `currentStreakDays` — Active consecutive run.
  - `longestStreakDays` — Personal best record (never wiped).
  - `cumulativeClearDays` — Total lifetime days accomplished across all runs (never wiped).
  - `totalMoneySaved` — `cumulativeClearDays * dailyCostSavings`.
  - `achievedMilestones` — Permanent ceremonial `MilestoneCoin` badges (`24h`, `3d`, `7d`, `14d`, `30d`, `60d`, `90d`, `180d`, `1yr+`).
  - `slipFraming()` — Non-shaming reset copy (*"Your record is X days. You are already Y days toward beating it."*).

### 3.2 Hardware-Bound Security & Privacy (`Brick 1`)
- **Partitioned Dual-Database Architecture:**
  - `streak_core.db` — Fast local journey metadata and widget sync.
  - `recovery_enc.db` — **SQLCipher (`net.sqlcipher`) AES-256** encrypted ledger storing all `check_ins`, HALT triggers, and private journal reflections.
- **`KeyStoreManager` & Device Credential Unlock:**
  - AES-256-GCM wrapping key stored in Android Hardware `StrongBox` (with `TEE` fallback) using `AUTH_BIOMETRIC_STRONG | AUTH_DEVICE_CREDENTIAL` (supports Fingerprint, Face Unlock, or Device PIN/Pattern/Password).
- **`FLAG_SECURE` & Zero Cloud Backup:**
  - Blocks OS screenshots, screen recordings, and Recent Apps switcher previews.
  - `allowBackup="false"` and strict `data_extraction_rules.xml` prevent Google Drive / cloud extraction.
- **Two Build Flavors:**
  - `core` — Zero `android.permission.INTERNET` in manifest (100% air-gapped APK).
  - `store` — Google Play edition (network permission used *exclusively* for Google Play Billing one-time unlock check and Play In-App Review).

### 3.3 4-Tier Check-In & HALT Context (`Brick 3`)
- **4-Tier Moment Read:**
  - 🟢 **Clear / On Track** — Instant reinforcement; increments active streak and lifetime days.
  - 🟡 **Passing Thought / Friction** — Opens optional **HALT** context row (`Hungry`, `Angry`, `Lonely`, `Tired`, `Stressed`, `Hopeless`) + encrypted reflection note.
  - 🟠 **White-Knuckling / Hard Day** — Logs HALT context and offers immediate transition to the **Reset** tab (Pocket Anchor, 4×4 Breather, or Mini-Games).
  - 🔴 **Critical** — Transitions immediately to the full-screen **Rescue Hub**.
- **Minimalist Monthly Heatmap Calendar:** 3-state visual calendar (`Clear`, `Urge Overcome`, `Off-Day Logged`) derived automatically from check-ins.

### 3.4 Somatosensory Reset & Visuospatial Engine (`Bricks 4 & 5`)
- **Tactile Pocket Anchor (1–60 min):** Eyes-free foreground haptic timer delivering a reassuring dual-pulse vibration every 60 seconds (micro-time-chunking), midpoint accent, and completion wave.
- **4×4×4×4 Box Breather & 4-7-8 Breathing Circle:** Synchronized visual + `VibrationEffect` haptic ramps (inhale ramp, hold micro-ticks, exhale decay) that work in silent mode without looking at the screen.
- **3 Offline Visuospatial Mini-Games (Jetpack Compose Canvas):**
  1. **Tile Merge (2048):** 4×4 spatial arithmetic grid.
  2. **Block Drop:** High-contrast monochrome polyomino line-clear puzzle (`withFrameNanos` loop).
  3. **Pattern Echo:** 4-quadrant sequence memory recall.
  - *Guardrails:* Zero gambling mechanics, instant exit/auto-save, gentle 3-minute check-in prompt.

### 3.5 Offline "Reflect" Vault (`Brick 6`)
- **100% In-Memory JSON (Zero DB/Network Latency):**
  - **365 KJV Scripture Verses** (`daily_verses.json`), emotion-tagged by HALT state, with Verse-of-the-Day and full calendar picker.
  - **130 Original Reflection Passages** (`passages.json`), browsable by theme or random draw.
  - **100 Original Daily Affirmations** (`affirmations_recovery.json` + `affirmations_spiritual.json`), opt-in via Settings.

### 3.6 Always-Free Crisis & Support Rescue Hub (`Brick 7`)
- One-tap offline `tel://` dialers for **Trusted Ally / Sponsor**, **Support Person**, **988 Lifeline**, **SAMHSA**, **Crisis Text Line**, and specialized helplines, plus `geo:` local support meeting finder and 2-second safe-exit confirmation.
- **Ethical Guarantee:** Never gated by the 7-day trial or paywall.

---

## 4. Visual & Video GTM Strategy: Why Human Hands-On Visuals Matter

1. **Why People Holding ClearStreak Is Essential:**
   - Pure UI recordings prove the software works, but **showing a real person holding ClearStreak in everyday moments** bridges the gap between *"recovery app"* and *"daily life companion for any journey"*.
   - **Scene Archetype 1 (Morning "Streak Doing"):** A user holding their phone beside morning coffee or running gear — checking in on a **30-Day Morning Run / Deep Work Streak**. Positions ClearStreak for high-agency builders and athletes.
   - **Scene Archetype 2 (Evening "Days Not Doing"):** A user relaxing in warm evening light, checking in on **42 Days Clear of Doomscrolling / Late-Night Sugar**, highlighting **Data Over Shame** and **100% On-Device Privacy**.
   - **Scene Archetype 3 (On-the-Go "Pocket Anchor"):** A user walking outdoors holding the phone casually at their side with **Pocket Anchor Haptic Grounding** active — visually demonstrating eyes-free tactile calm.

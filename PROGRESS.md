# ClearStreak — Build State & Decision Log

**Purpose:** durable record of what has been built, every decision made, and what remains — so no context is lost between sessions. Read alongside `ClearStreak_Spec_v2.md` (original spec) and `ClearStreak_Blueprint_v1.md` (the newer authority for direction). Where they conflict, order of authority is: **this log + blueprint > spec**.

*Last updated: 2026-08-18 · Branch: `main` · DB schema version: 4 · CI: green (coreDebug + storeRelease)*

---

## 1. Snapshot

- **Platform:** Android only (Kotlin + Jetpack Compose). iOS is spec'd but not built.
- **Package:** `com.eight64zeros.clearstreak` (the spec's `com.864zeros…` is an invalid Java package — a segment cannot start with a digit).
- **Architecture:** single-Activity (`MainActivity : FragmentActivity`), manual `when(currentRoute)` navigation via Compose state (no Nav-Compose graph).
- **Repo:** https://github.com/864zeros/clearstreak · CI builds `coreDebug` + `storeRelease` on every push and uploads both APKs.

---

## 2. What is built (inventory)

### Security / privacy (Brick 1 foundation — complete)
- **Two databases, partitioned:**
  - `streak_core.db` — plaintext `SQLiteOpenHelper`. Table: `journeys`. (The `coping_cards` table was removed — see §4.)
  - `recovery_enc.db` — SQLCipher (`net.sqlcipher`) AES-256, opened only after biometric unlock. Table: `check_ins` (holds encrypted journal notes).
- **`KeyStoreManager`** — AES-256 GCM key in AndroidKeyStore; `setUserAuthenticationRequired(true)`, `setInvalidatedByBiometricEnrollment(true)`, StrongBox with TEE fallback on `StrongBoxUnavailableException`.
- **`DatabasePassphraseProvider`** — generates a random 32-byte DB passphrase, encrypts it with the biometric-gated `Cipher` (via `BiometricPrompt.CryptoObject`), stores ciphertext + IV in prefs.
- **`FLAG_SECURE`** set in `onCreate`; `allowBackup=false`; `data_extraction_rules.xml` excludes the encrypted DB + secure prefs from cloud backup.
- **Flavors:** `core` (no `INTERNET` permission, air-gapped) / `store` (overlay adds `INTERNET` + `BILLING`).

### Data / ledger
- **Models:** `Journey` (+`suppressGameTools`), `CheckIn` (no more `copingCardId`), `Milestone` + `MilestoneTier`, `StreakStats` (+`achievedMilestones`), enums `UrgeLevel` / `HaltTrigger` / `JourneyCategory`.
- **`StreakCalculator`** — the recovery ledger. Computes current/longest/cumulative days, money saved, next-milestone + progress, `achievedMilestones` (permanent), and `slipFraming()` copy.
- **`SharedStreakStorage`** / **`EmergencyContactStorage`** — SharedPreferences for widget sync and sponsor/support contacts.

### UI (all present, build green)
- **Screens:** Dashboard, CheckInModal, CrisisIntercept, JourneyDetail, Journal, Settings, AddJourney, BiometricLock.
- **Components:** UrgePulseGrid, HaltTriggerRow, BreathingCircle (4-7-8 + haptics), GroundingTimer (60s), OIAButton/Card/TextField.
- **Theme:** OIA design system — full palette, SansSerif type scale, shape/spacing tokens.
- **Widget:** Glance `ClearStreakWidget` reads `SharedStreakStorage` (streak + next milestone; taps open the app).

---

## 3. Session commit log (most recent first)

| Commit | Summary |
|---|---|
| `49b3063` | **Brick 2**: milestone badges + non-shaming slip framing |
| `77d1780` | Tear down coping-cards feature (blueprint §2 pivot) — 12 files, −738 lines |
| `9fc9f03` | **Brick 1**: add `suppress_game_tools` to the recovery ledger |
| `064e29a` | Gitignore the copyrighted reference PDF |
| `17a9159` | Add strategic blueprint; gate all mini-games off gaming journeys |
| `194c91b` | Add `storeRelease` build to CI (R8 minify + shrink verification) |
| `2d566a2` | Fix ProGuard keep rules to correct SQLCipher package (`net.sqlcipher.**`) |
| `6ae6b80` | Fix check-in crash bugs (`mood_score` column; category CHECK constraint) |

Pre-session scaffold commits: `cd2278e`, `c561d52`, `4721f23`, `a959341`, `8ef9a48`.

---

## 4. Brick status (modular build sequence)

| Brick | Status | Notes |
|---|---|---|
| **1 — Encrypted Core DB & Recovery Ledger** | ✅ Done | Foundation pre-existed; added `suppress_game_tools` end-to-end. |
| **2 — Rewards, Milestones & Non-Shaming Slip Framing** | ✅ Done (visual/data) | Tactile milestone pulse deferred to Brick 4. |
| **3 — Minimalist Calendar & Progress Visualizer** | ✅ Done | Per-journey month heatmap on the detail screen; derives 3 states from the 4-tier model. |
| **4 — Somatosensory Reset (Pocket Anchor & 4×4 Breather)** | ✅ Done | `HapticEngine`, Pocket Anchor timer, 4×4 box breather, "Reset" tab; screen kept awake during sessions. Background service **descoped** (phone-on assumption). |
| **5 — Offline Visuospatial Mini-Games** | ⬜ Not started | Fully spec'd; gated off gaming journeys; Block Drop = IP-attention item. |
| **6 — Offline Heritage Vault (Proverbs, 1939 Big Book, FTS5)** | ⬜ Not started | Big Book is **counsel-gated** (§5). Proverbs/Serenity Prayer are clean. |
| **7 — Unified Crisis Intercept Hub** | 🟡 Largely exists | `CrisisInterceptScreen` built (tel:// dialers, geo, 2-sec safe-exit, breathing, grounding). |

**Coping cards (old model):** ❌ removed. Replaced by Bricks 4–6 per the §2 pivot.

---

## 5. Decision register (the important part)

### Product / UX
- **Content pivot (blueprint §2):** the acute-craving intervention is **not** text advice. It is haptics (Brick 4) + visuospatial mini-games (Brick 5) + public-domain heritage content (Brick 6). The entire coping-card feature was torn down.
- **No ingestion, ever.** No card/tool suggests eating, drinking, chewing, or consuming anything. Rationale: medical liability (unknown meds/allergies/ED history), substitution-addiction risk, and irrelevance to gambling/behavioral journeys.
- **Awareness statements, not commands.** Any user-facing intervention text states a truth for the user to hold ("The urge always ends the same way", "You are always free to leave") — never an imperative instruction the app owns.
- **Data Over Shame.** Slips reset the active streak but never erase history. `achievedMilestones` are permanent; `slipFraming()` never uses "failure/lost/broken".
- **Calendar reconciliation (decided).** The check-in flow keeps the 4-tier urge model + HALT. The progress calendar (Brick 3) does **not** change the data model — it derives a 3-state heatmap per day by priority: **Slip** (any slip that day) > **Urge overcome** (a non-CLEAR urge, no slip) > **Clear** (only CLEAR check-ins); no check-in = empty. HALT survives, shown in the per-check-in history, not the calendar.
- **Games gated OFF gaming/screen journeys.** ALL mini-games (not just Block Drop) are suppressed when the active journey is gaming/screen-compulsion recovery — otherwise the tool feeds the habit. Mechanism: per-journey `suppress_game_tools` flag (defaults **on** for `BEHAVIORAL`, user-settable at creation). Fallback for those journeys = haptic + heritage tools.
- **Zero gambling mechanics** in any mini-game (no reels/loot-crates/casino SFX) — critical for gambling-recovery users.
- **Clinical framing is design rationale, not marketing.** The Elaborated-Intrusion / craving-reduction science justifies the games internally but must **not** appear as store-listing claims (App Store/Play medical-claims rejection risk, spec §12).

### Legal / content sourcing
- **Copyright principle:** techniques/methods are **not** copyrightable — only specific expression is. So the plan is *original wording of public-domain techniques*, which sidesteps copyright entirely.
- **Clean sources:** US-government works (VA, SAMHSA, NIMH, CDC — public domain per 17 U.S.C. §105); the **Bible** (KJV/WEB, genuinely PD); the **short-form Serenity Prayer** (low risk). Avoid CC-BY-SA (viral ShareAlike) and CC-NC (we sell the app).
- **AA "Big Book" — counsel-gated, NOT "100% legal".** Its public-domain status is **contested, US-only, and actively disputed by AAWS** (who publish a © notice for 1939/1955/1976/2001). The **1939 1st edition** has the stronger non-renewal argument (better than the 1955 2nd edition). Using it **reverses spec §9.1** (which excluded all AA literature). Personal stories in the back may retain separate status → bundle only the core 164 pages. **Verify renewal via the US Copyright Office / Catalog of Copyright Entries and get IP-counsel sign-off before shipping.** The real-world PD compilation ("Basic Texts") is **The Anonymous Press** (anonpress.org).
- **The "30 Tools to Stay Sober" PDF is copyrighted** (a free-circulating handout ≠ public domain). It is **gitignored** and kept as a local idea-reference only; any card ideas derived from its generic concepts are tagged `ORIGINAL`.

### Engineering
- **Package** `com.eight64zeros.clearstreak` (see §1).
- **ProGuard** keeps `net.sqlcipher.**` (the artifact `net.zetetic:android-database-sqlcipher` ships that package). Verified by the CI `storeRelease` (R8) build.
- **Encrypted DB is never migrated destructively.** When coping cards were removed, only the plaintext `coping_cards` table was dropped (DB_VERSION 3→4). The encrypted `check_ins` table keeps its now-vestigial `coping_card_id` column (unused, never read/written) to avoid touching user-encrypted data.
- **CI** verifies both `coreDebug` (non-minified, air-gapped) and `storeRelease` (minified). The `release` build type signs with the debug config, so CI needs no signing secrets.
- **Pocket Anchor stays foreground.** The design assumes the phone/screen is on during a session, so there is no background foreground-service. Active grounding tools keep the screen awake via `View.keepScreenOn` — no `POST_NOTIFICATIONS`, no foreground-service type, no Play-policy review. (This descopes the former "Brick 4b".)

---

## 6. Open items / backlog

**Brick 4b (screen-off Pocket Anchor): DESCOPED (2026-08-18).** The design assumption is the phone/screen is on during a grounding session, so the foreground-service / screen-off path is **not** built. Instead the tools keep the screen awake (`View.keepScreenOn`) while running — no background service, no `POST_NOTIFICATIONS`, no foreground-service type, no Play-policy review.

**Deferred (flagged, not forgotten):**
- **Milestone celebration haptic pulse** — the `HapticEngine` now exists (Brick 4); still needs a "last-celebrated milestone" persistence hook to fire once per crossing.
- **Slip framing immediacy** — currently shows on `JourneyDetailScreen` only. Showing it *right after* a slip is logged needs the check-in flow to pass `stats` to the modal (plumbing).
- **Play Billing (IAP)** — the P0 one-time unlock. `store` flavor has the `BILLING` permission but no billing code.
- **Midnight rollover** — a `WorkManager`/`AlarmManager` job to refresh streak + widget without opening the app (widget is currently only fresh after an app open writes to `SharedStreakStorage`).
- **Release signing** — `storeRelease` is debug-signed (fine for CI/testing, cannot go to Play). Needs a real keystore + guarded signing config before submission.
- **CI action deprecations** — bump `actions/setup-java@v4`→`v5` (and the Node-20 actions) to clear warnings.
- **Tests** — none exist (`testInstrumentationRunner` is configured but no test sources). `StreakCalculator` is the obvious first JVM unit-test target.

---

## 7. Key file map

```
app/src/main/java/com/eight64zeros/clearstreak/
├── MainActivity.kt            # biometric gate + manual route switch
├── ClearStreakApp.kt          # SQLCipher loadLibs()
├── security/                  # KeyStoreManager, DatabasePassphraseProvider
├── database/DatabaseManager.kt# both DBs, DAO, schema + migrations (v4)
├── model/                     # Journey, CheckIn, Milestone, StreakStats, Enums
├── data/                      # StreakCalculator (ledger), Shared/Emergency storage
├── widget/                    # Glance widget + receiver
├── navigation/Screen.kt       # route sealed class
└── ui/ (theme, components, screens)

Root docs:
├── ClearStreak_Spec_v2.md       # original spec (older)
├── ClearStreak_Blueprint_v1.md  # strategic blueprint (direction authority)
├── PROGRESS.md                  # this file (state + decision log)
└── README.md                    # ⚠️ OUTDATED — still describes coping-cards architecture
```

> **Note:** `README.md` has not been updated for the §2 pivot / teardown — it still lists `CopingCardsSeed`, the coping library, and the old file tree. Update it before treating it as accurate.

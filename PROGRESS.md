# ClearStreak — Build State & Decision Log

**Purpose:** durable record of what has been built, every decision made, and what remains — so no context is lost between sessions. Read alongside `ClearStreak_Spec_v2.md` (original spec) and `ClearStreak_Blueprint_v1.md` (the newer authority for direction). Where they conflict, order of authority is: **this log + blueprint > spec**.

*Last updated: 2026-08-19 (evening) · Branch: `main` @ `9daf48d` · DB schema version: 5 · CI: green (coreDebug + storeRelease)*

**Recent (post-brick refinements):** category taxonomy → Alcohol/Drugs/Vape/Gambling/Behavioral/Custom (+ per-journey `customLabel`, wrapping pill selector, DB v5); Add-Journey start-date picker + no auto-seeded default journey; verse-of-the-day on Home (Settings toggle); plain-language "Your Privacy" + a "The Science" screen (real citations); Reset tab reordered (Pocket Anchor → Games → Breathing) with the 4-7-8 breather added and per-tool blurbs; game score removed. Rescue screen gained Crisis Text Line + smoking/gambling helplines. **Big Book passages** (130 re-authored, original content — no clinical gate, no verbatim reader) shipped as `assets/passages.json` + `BookPassage`/`PassageStore`; the Faith tab became **"Reflect"** (Scripture + Recovery segments, theme browse, random passage) and the Reset tab gained a "Words for this moment" passage card. **`content/` and `app/src/main/assets/big_book.json` are gitignored (counsel-gated / source-only) — use explicit `git add`, never `git add -A`.**

**Latest batch (2026-08-19 evening):** Home reordered so **journeys sit at the top** (the journey is primary; verse + passage-of-day are supplemental, moved below); **Rescue always shows the sponsor + support person** (prompts "Not set — add in Settings" when unconfigured, never hides the resource); **`OIATextField` forces dark input text/cursor** so entries stay readable on a dark-mode phone (band-aid — the app is still light-only; real dark support is deferred to the build-kit). **Git history rewritten (force-push `ee85334→9daf48d`):** the counsel-gated `content/aa-bigbook-1939-full-PD.epub` **and** `app/src/main/assets/big_book.json` were purged from all 46 commits via `filter-branch` (0 refs, 0 blobs remain; local gitignored copies preserved). GitHub may retain unreachable objects server-side until its own GC — a Support GC request would be needed for a guaranteed remote purge.

---

## ▶ Session handoff — start here (2026-08-19 evening)

**First thing: install the freshest APK and verify the latest fixes.**
Get it from **GitHub → Actions → latest green run → Artifacts → `clearstreak-core-debug-apk`** (unzip → install; phone needs an enrolled fingerprint/face or you're stuck at the lock screen; screenshots are blocked by `FLAG_SECURE` — both by design).

Verify:
1. **Home is journeys-first** — after adding a journey, its card(s) + "Add Another Journey" sit at the top; verse + passage-of-day are below.
2. **Journal/journey text is readable** — type into any field (works regardless of your phone's dark/light setting; the app itself is light-only).
3. **Rescue shows the sponsor + support person** even before you've set them (they read "Not set — add in Settings").

**Product decisions locked this session:**
- **No dark theme in the app.** Deferred wholesale to the forthcoming **864zeros-mobile-build-kit** (the app stays light-only; `OIATextField` is the only theme-hardening in place).
- **Faith → "Reflect"** (Scripture + Recovery segments) is the shipped direction; passages are contextual in Reset + optional on Home.
- **Big Book content is history-scrubbed** (see top matter) — this backlog item is now **closed**.

**Charging model (locked 2026-08-20, post market-research):** **7-day free trial → $14.99 one-time unlock** via Play Billing (no subscription). Try-before-you-buy, so there is no permanent free feature tier. Built: `billing/` (PremiumManager + StoreBillingManager + CorePremiumManager + TrialStatus) and the `UnlockScreen` paywall; trial gate in `MainActivity`. **The crisis Rescue hub always stays reachable even when locked** (ethical line). Trial anchored to `PackageManager.firstInstallTime`.

**Path-to-launch — the remaining blockers (see §6):**
1. **Play Console setup (user)** — create the account ($25 + ID verification) and define product `clearstreak_unlock` as a one-time product at **$14.99**; add a license tester. Billing code is done and CI-green; it just can't be *tested* until the SKU exists.
2. **Real release keystore** — `storeRelease` is debug-signed; cannot go to Play.
3. **Store-readiness** — privacy policy, listing copy (see `overview.html`), health-category medical-claims framing, content rating. Also fix the store-flavor "no internet at all" privacy copy (store build has INTERNET for billing).

**Polish (trails the launch):** milestone-hit haptic pulse, crisis-intercept usage logging, slip-framing immediacy, midnight `WorkManager` widget refresh, verse emotion-tag review, ch01/ch02 passage over-mining trim, CI `setup-java` v4→v5 bump, first `StreakCalculator` unit test.

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
| **5 — Offline Visuospatial Mini-Games** | ✅ Done | Portable `game/` package: Tile Merge, Pattern Echo, Block Drop — all gated, host with picker + time-box banner. |
| **6 — Offline Heritage Vault (Faith tab)** | ✅ Done | In-memory `daily_verses.json` (no DB). Faith tab: Serenity Prayer · verse-of-the-day · verse calendar (tap a date → its verse) · Randomize. **365 KJV verses** (public domain, extracted from the bundled KJV PDF), each HALT-emotion tagged. 1939 Big Book **excluded** (counsel-gated). |
| **7 — Unified Crisis Intercept Hub** | ✅ Reviewed | `CrisisInterceptScreen`: tel:// dialers (sponsor/support/SAMHSA/988, no CALL permission), geo meeting finder (generalized off AA-only), 2-sec safe-exit, breathing + grounding. Matches current decisions. |

**Coping cards (old model):** ❌ removed. Replaced by Bricks 4–6 per the §2 pivot.

---

## 5. Decision register (the important part)

### Product / UX
- **Content pivot (blueprint §2):** the acute-craving intervention is **not** text advice. It is haptics (Brick 4) + visuospatial mini-games (Brick 5) + public-domain heritage content (Brick 6). The entire coping-card feature was torn down.
- **No ingestion, ever.** No card/tool suggests eating, drinking, chewing, or consuming anything. Rationale: medical liability (unknown meds/allergies/ED history), substitution-addiction risk, and irrelevance to gambling/behavioral journeys.
- **Awareness statements, not commands.** Any user-facing intervention text states a truth for the user to hold ("The urge always ends the same way", "You are always free to leave") — never an imperative instruction the app owns.
- **Data Over Shame.** Slips reset the active streak but never erase history. `achievedMilestones` are permanent; `slipFraming()` never uses "failure/lost/broken".
- **Calendar reconciliation (decided).** The check-in flow keeps the 4-tier urge model + HALT. The progress calendar (Brick 3) does **not** change the data model — it derives a 3-state heatmap per day by priority: **Slip** (any slip that day) > **Urge overcome** (a non-CLEAR urge, no slip) > **Clear** (only CLEAR check-ins); no check-in = empty. HALT survives, shown in the per-check-in history, not the calendar.
- **Games on by default (revised 2026-08-19).** Originally ALL mini-games were gated OFF gaming/screen journeys via a per-journey `suppress_game_tools` flag (default-on for `BEHAVIORAL`, global hide). **Reverted** — that silent opt-out hid games entirely and read as "the games are missing." Games now always show on the Reset tab; the Add-Journey toggle was removed; the flag/column is retained but unused. Gaming-protection will return as a **visible, opt-in, per-journey** control at the check-in moment (not a silent global hide).
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
- **Games are a self-contained, portable module.** The `com.eight64zeros.clearstreak.game` package has **zero imports** from the recovery domain (model/data/database/security) — the engine is pure Kotlin, the board is pure Compose — so the game cores could be lifted into a standalone game app. Dependency flows one way: ClearStreak `ui` → `game`, never the reverse. ClearStreak-specific concerns (time-box banner, gating, scoring-into-history) live in a thin `ui` wrapper (`MiniGamesCard`).
- **Games on by default (global gate removed 2026-08-19).** The "hide games if any journey is gaming" rule was removed — it hid all games **silently** (opt-out, invisible) and made testing think the games were missing. Games now always show on the Reset tab, and the confusing "Screen / gaming recovery" toggle was removed from Add Journey. The `suppressGameTools` field/column is retained (no migration) but unused; proper gaming-protection will return as a **visible, opt-in, per-journey** control surfaced at the check-in moment.
- **Block Drop trade-dress.** Differentiated from Tetris per *Tetris Holding v. Xio* (2012): the name "Block Drop", a non-canonical **8×16** well (not 10×20), a **monochrome slate** palette (no 7-color piece mapping), and **no ghost piece / next-preview / hold**. The polyomino shapes themselves are uncopyrightable math.
- **Heritage Vault storage (rebuilt 2026-08-19).** No database at all now — `HeritageStore` loads the bundled `assets/daily_verses.json` (365 records) into memory via `org.json`. This dropped SQLite entirely (an earlier FTS5 `heritage.db` crashed on devices without the module; even the LIKE-table version was unnecessary). Verses are **KJV, public domain**, extracted locally from the bundled `The-Holy-Bible-King-James-Version.pdf` with `pdftotext` + a `{chapter:verse}` index keyed by the canonical 66-book order (365/365 hit). Source PDF and the original NIV JSON are gitignored — only the derived KJV asset ships. The 1939 AA Big Book remains excluded (counsel-gated, §5).
- **Verse emotion tags are first-pass.** Each daily verse carries a HALT-aligned `emotion` (STRESSED/LONELY/TIRED/HOPELESS/ANGRY/GENERAL) from a keyword heuristic (172 GENERAL default, 193 specific). Not yet used in the UI; **needs a pastoral/human review pass** before it drives any mood-matched surfacing.

---

## 6. Open items / backlog

**Brick 4b (screen-off Pocket Anchor): DESCOPED (2026-08-18).** The design assumption is the phone/screen is on during a grounding session, so the foreground-service / screen-off path is **not** built. Instead the tools keep the screen awake (`View.keepScreenOn`) while running — no background service, no `POST_NOTIFICATIONS`, no foreground-service type, no Play-policy review.

**Deferred (flagged, not forgotten):**
- **Crisis-intercept usage isn't logged.** The Crisis screen has no DB access by design, so a 🔴 intercept doesn't write a `check_in` with `is_crisis_intercept=1` (the spec's success metric). Also `BreathingCircle` / `GroundingTimer` predate `HapticEngine` and could be unified onto it.
- **Milestone celebration haptic pulse** — the `HapticEngine` now exists (Brick 4); still needs a "last-celebrated milestone" persistence hook to fire once per crossing.
- **Light/dark theme — DEFERRED TO THE 864zeros-mobile-build-kit (decided 2026-08-19).** The app stays **light-only**; we will *not* build dark mode here. Screens hardcode the light OIA palette; `OIATextField` forces dark input text so entries stay readable on a dark-mode phone (the one hardening we keep). A real dark theme (mechanical `colorScheme` refactor of every screen) is out of scope for ClearStreak and folds into the reusable build-kit instead.
- **~~Scrub the 1939 Big Book content from git history~~ — DONE (2026-08-19).** `content/aa-bigbook-1939-full-PD.epub` and `app/src/main/assets/big_book.json` were purged from all 46 commits via `git filter-branch` and force-pushed (`ee85334→9daf48d`); 0 refs / 0 blobs remain locally, gitignored source copies preserved. Residual: GitHub may keep unreachable objects until its own GC — a Support GC request would guarantee remote removal if ever needed.
- **Store-readiness (launch gate).** Beyond code: a hosted **privacy policy** URL, Play listing copy + graphics, **health-category medical-claims framing** (avoid treatment claims), content rating questionnaire, and data-safety form. None started.
- **Repeatable Android app-creation process / 864zeros-build-kit.** Codify this project's reusable scaffold into (or alongside) the **864zeros-build-kit** so future Android apps start from a template: the air-gapped `core`/`store` flavor split, SQLCipher + biometric `CryptoObject` gate (StrongBox/TEE key), the OIA design-system theme, the Glance widget, the Gradle + `libs.versions.toml` setup, and the GitHub Actions build (`coreDebug` + `storeRelease` + artifact upload). ClearStreak is the reference implementation.
- **Slip framing immediacy** — currently shows on `JourneyDetailScreen` only. Showing it *right after* a slip is logged needs the check-in flow to pass `stats` to the modal (plumbing).
- **~~Play Billing (IAP)~~ — DONE (2026-08-20).** 7-day free trial → $14.99 one-time unlock (`clearstreak_unlock`) via Play Billing 8.3.0, flavor-split (`billing/`), with the always-free crisis escape. Remaining: user must create the Play Console SKU to test a live purchase (see §handoff).
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

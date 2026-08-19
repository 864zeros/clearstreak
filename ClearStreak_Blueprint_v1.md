# ClearStreak — Strategic Build Blueprint

**864zeros Local-First Utility | Pillar: Faith, Health & Growth**
*Companion to `ClearStreak_Spec_v2.md`. This blueprint records the pre-build architecture, mechanics, and decision register. Where this document and the spec disagree, this blueprint is the newer authority.*

*Last updated: 2026-08-18*

---

## 1. Core Architecture & Privacy Guardrails

- **Local-First & Air-Gapped:** Core build excludes `android.permission.INTERNET` entirely. Zero cloud accounts, zero telemetry, zero analytics, zero central databases.
- **Hardware-Bound Security:** SQLCipher (AES-256) encrypted database with keys tied to device biometrics (StrongBox/TEE on Android; Secure Enclave on iOS).
- **"Data Over Shame" Engine:** Slips reset the active streak counter but never erase cumulative history. The database permanently preserves cumulative clean days, previous milestone stretches, and the personal best record.
- **Anti-SaaS Pricing:** $4.99 one-time Core Unlock + optional $9.99 Supporter Tier. No recurring subscriptions.

---

## 2. Acute Craving & Grounding Engine (Zero Ingestion, Zero Advice)

Instead of passive text advice or ingestion-based coping cards, ClearStreak uses **eyes-free somatosensory feedback** and **visuospatial working-memory disruption**. Any user-facing text is an **awareness statement, never an instruction/command.**

- **Tactile Pocket Anchor (1–60 min):** Foreground in-app tool — the design assumption is the phone/screen is on during a session, and the screen is kept awake while it runs (no background service). Dual-pulse vibration every 60 s (micro-time-chunking), a midpoint accent, and a resolving wave on completion.
- **4×4×4×4 Tactile Box Breather:** Inhale = smoothly ramping intensity; Hold = subtle 1 s micro-ticks; Exhale = decaying wave; Hold-empty = soft resting pulse. *(Haptic ramps are expressed via `VibrationEffect` amplitude + composition primitives; exact frequency is hardware-dependent, not literal Hz.)*
- **Visuospatial Working-Memory Mini-Games:** Lightweight offline Canvas engines (see Brick 5).

---

## 3. Local-First Faith & Heritage Suite (100% Offline)

- **Proverb-a-Day:** Bundled SQLite table with all 31 chapters of Proverbs (Public Domain WEB/KJV). Auto-maps `day_of_month` (1..31) → chapter (1..31). **Legally clean.**
- **The Serenity Prayer:** Short-form public-domain text, featured above emergency tools on the Crisis Intercept screen and in the offline grounding tab. **Low risk.**
- **1939 AA 1st Edition + FTS5 Search:** Bundled core 164 pages with local full-text search (SQLite FTS5). ⚠️ **Counsel-gated — NOT "100% legal."** The Big Book's public-domain status is contested, US-only, and actively disputed by AAWS (who publish a © notice covering 1939/1955/1976/2001). This reverses spec §9.1, which excluded all AA literature. Ship only after IP-counsel confirmation of the 1939 renewal status; bundle core 164 pages only (personal stories may retain separate status).

---

## 4. Visual Analytics & Milestones

- **Minimalist Dot/Heatmap Calendar:** High-contrast monthly grid — 🟢 Clear, 🟡 Urge Overcome, ⚪ Slip Logged. *(Reconcile with the 4-tier urge model in the check-in flow.)*
- **Milestone Celebrations:** Badges and tactile pulses for daily, weekly, monthly, and yearly achievements.
- **Slip Framing:** Immediate contextual calculation on reset — *"Your record is X days. You are already Y days toward beating it."*

---

## 5. Unified Crisis Intercept (Red Rescue Hub)

- Fullscreen emergency fail-safe triggered by 🔴 Critical urge state.
- Hardcoded, offline `tel://` dialers (Sponsor, 988, SAMHSA) and `geo:` map queries.
- 2-second hold required on "I Am Safe" to exit back to the app.

---

## 6. Brick 5 — Visuospatial Mini-Games (Detail)

### Clinical mechanism
Per **Elaborated Intrusion (EI) Theory**, cravings depend on visual/spatial working memory to sustain vivid imagery. 3–5 minutes of a visuospatially demanding task competes for that limited bandwidth and diminishes craving vividness/intensity. Evidence is **strongest for mental-rotation/visuospatial load (Block Drop)**; 2048 and Pattern Echo are plausible but less directly evidenced. Effect sizes are modest — **design rationale only, not marketing copy** (avoids App Store medical-claims rejection, spec §12).

### Game suite

| GAME 1: 2048 (Merge Grid) | GAME 2: Block Drop (Polyomino Line Clear) | GAME 3: Pattern Echo (Sequence Memory) |
|---|---|---|
| 4×4 matrix; spatial slide + arithmetic | Falling polyominoes rotated into cleared rows | 4-quadrant flash-and-repeat recall |
| MIT open-source logic (Cirulli) | Uncopyrightable polyomino math (Golomb 1953) | Generic 1970s sequence-memory logic |
| **IP: clean** | **IP: trade-dress caution (see below)** | **IP: clean** |

- **2048:** Mechanic isn't copyrightable; native reimplementation. Name is generic. Lowest risk.
- **Block Drop:** Mechanics are free, but per *Tetris Holding v. Xio* (2012) the **audiovisual trade dress is protected**. Guardrails: **do not use the word "Tetris"; use a custom high-contrast monochrome/slate palette; avoid the canonical 10×20 well proportions and the 7-color piece mapping, ghost piece, and preview presentation** called out in that case. Highest-attention IP item.
- **Pattern Echo:** Repeat-the-sequence mechanic is generic and predates "Simon" (Hasbro TM). Distinct name + generic quadrants. Low risk. Per-quadrant differentiation must use **distinct patterns/durations + visual position**, not frequency (hardware-limited).

### Universal game guardrails
1. **Zero gambling mechanics** — no spinning reels, slot wheels, loot crates, or casino SFX (critical for gambling-recovery journeys).
2. **Instant exit & auto-save** — state saves immediately on "Done" or interruption, no penalty. A craving tool must never trap the user.
3. **Session time-boxing (3–5 min)** — after 3 min, a gentle, non-coercive banner (a question/offer, not a command): *"Craving wave peaked? Tap to check in or keep playing."*
4. **100% offline, native Canvas** — Jetpack Compose Canvas (Android) / SwiftUI Canvas or SpriteKit (iOS). No WebViews, no JS bundles, no network, no ads. Block Drop requires a frame-timed game loop (`withFrameNanos`).

---

## 7. Journey ↔ Tool Compatibility (Game Gating)

**Decision (2026-08-18): ALL mini-games (2048, Block Drop, Pattern Echo) are gated OFF gaming/screen-compulsion journeys.**

**Rationale:** Offering an engaging mini-game as the intervention to a user recovering from compulsive gaming or screen use is a contraindication — the same class of problem that removed "eat something" for eating-disorder safety. Any game feeds the exact behavior the journey is trying to interrupt, so the entire mini-game suite is suppressed for these journeys — not just Block Drop.

**Mechanism (there is no dedicated "gaming" category today):** Gaming/screen recovery currently lives under `BEHAVIORAL` (📱 "Behavioral & Habits") or `CUSTOM`. Add a per-journey flag `suppress_game_tools` on the Journey model:
- Defaults **on** for `BEHAVIORAL` journeys and user-settable at journey creation ("Is this a screen/gaming recovery?").
- When the **active** journey has `suppress_game_tools = true`, the Acute Craving Engine **hides the entire mini-game suite (2048, Block Drop, Pattern Echo)** and falls back to non-game tools: **Pocket Anchor**, **4×4 Breather**, and the **Faith & Heritage / awareness** content.

**Touches:** `Journey` model (+`suppress_game_tools`), journey-creation UI (screen/gaming question), `streak_core.db` `journeys` schema (new column + `DB_VERSION` bump), and the Brick 5 tool-selection logic.

---

## 8. Modular Build Sequence

```
[Brick 1: Encrypted Core DB & Recovery Ledger]
       │
       ├──► [Brick 2: Rewards, Milestones & Non-Shaming Slip Framing]
       ├──► [Brick 3: Minimalist Calendar & Progress Visualizer]
       ├──► [Brick 4: Somatosensory Reset (Pocket Anchor & 4×4 Breather)]
       ├──► [Brick 5: Offline Visuospatial Mini-Games (gated per §7)]
       ├──► [Brick 6: Offline Heritage Vault (Proverbs DB, 1939 Big Book, FTS5)]
       └──► [Brick 7: Unified Crisis Intercept Hub]
```

**Reconciliation note:** Bricks 1 and 7 (encrypted DB, crisis hub), the biometric gate, streak calculator, multi-journey model, and Glance widget **already exist** in the current codebase. Treat the sequence as *reconcile + extend*, not greenfield. Items the blueprint drops or leaves unassigned: the coping-card table / seed / CSV / Python pipeline (obsolete under §2), the HALT taxonomy and 4-tier check-in modal (needs an explicit keep/simplify/drop decision), the Glance widget, and IAP (in pricing but owned by no brick).

---

## Decision Register

| Date | Decision | Notes |
|---|---|---|
| 2026-08-18 | Acute-craving intervention pivots from text coping-cards to haptics + mini-games + PD heritage content | Removes content-liability and copyright exposure; card table becomes obsolete |
| 2026-08-18 | No ingestion, ever; any card text is an awareness statement, not a command | Applies to all user-facing intervention text |
| 2026-08-18 | 1939 Big Book is counsel-gated, not "100% legal" | Contested/US-only/AAWS-disputed; reverses spec §9.1 |
| 2026-08-18 | **All mini-games (2048, Block Drop, Pattern Echo) gated OFF gaming/screen journeys** via per-journey `suppress_game_tools` | Entire suite suppressed; falls back to haptic + heritage tools |

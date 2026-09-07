# Session Snapshot — 2026-08-20 — Affirmations content

## Session Header
- **Topic:** Authoring the real ClearStreak affirmations content (recovery + spiritual pillars).
- **State at start:** The Android app already had affirmation *plumbing* built (2026-08-20: `AffirmationStore`, `Affirmation` model, a Home "bell banner"), but it was fed a **placeholder set pending the referenced Big Book + Bible pipeline** (`PROGRESS.md:28`). No real affirmation content existed on disk.
- **State at end:** Two real, fully-authored affirmation content files exist (50 recovery + 50 spiritual), each line original 864zeros writing derived from and citing a public-domain source; a reproducibility style note was written; and all review-gate flags were removed per user directive. App-side wiring already consumes the two files.

---

## What Was Built

Context for a zero-context reader: **ClearStreak** is an Android (Kotlin/Compose) recovery app under `C:\dev\clearStreak\`. Its `content/` directory is the content-authoring workspace; the app bundles *copies* of shipping content under `app/src/main/assets/`. This session worked entirely in `content/` (plus memory), authoring a new content type called **affirmations**.

### `content/affirmations_recovery.json` (created)
- 50 short, second-person affirmations in a **secular** voice. Structure: top-level `_meta` + `affirmations[]`. Each record: `id` (`aff_recovery_NNN`), `text`, `pillar` (`"recovery"`), and a `source` block mirroring `passages_core.json`: `book` (`"AA-1939"`), `chapter_id`, `chapter_title`, `page`, `paragraph_index`, `quote_stub`.
- Each affirmation is an **original interpretation** of one specific 1939 Big Book passage (public domain), not a quotation.
- Spread across every mined chapter (The Doctor's Opinion → Ch XI). Secular re-mapping held: no mandatory faith, no moralizing words, gender-neutral, "use / act on the urge" not "drink"; Twelve Steps never distilled.
- Validated: parses, 50 records, 50 unique ids, no dup, every record has a source, no empty text.

### `content/affirmations_spiritual.json` (created)
- 50 affirmations in a **devotional** voice. Same shape, but `source` mirrors `clearStreak-daily-verse.json.json`: `citation` (e.g. `"Isaiah 41:10"`) + `verse_text` (**exact KJV**, public domain).
- Each is an original interpretation of one KJV verse; verse text quoted accurately.
- Coverage spans anxiety, peace, strength, hope, renewal, perseverance, refuge — deliberately broader than the daily-verse file's 3 categories.
- Validated identically: 50/50, unique, sourced, non-empty.

### `content/_src/AFFIRMATIONS_STYLE.md` (created)
- Reproducibility/voice contract for anyone adding affirmations later. Codifies: the one rule ("authored, not quoted — but always derived and always referenced"), the abstraction stance (no `moment`/`halt`/`urge` labels), voice per pillar, record shape, provenance discipline, and **"Review gates — none."**

### `C:\Users\Jeff\.claude\projects\C--dev-clearStreak\memory\no-clinical-review.md` + `MEMORY.md` (created)
- Durable project rule: ClearStreak content carries **no review gates** (neither `needs_clinical_review` nor `needs_theological_review`); the user is the author/reviewer.

### App-side files (NOT built this session — pre-existing; relevant context)
These already existed and already consume the two content files; only listed so the next session knows the consumer side:
- `app/src/main/java/com/eight64zeros/clearstreak/data/AffirmationStore.kt` — loads both pillar assets into an in-memory list (`AffirmationStore.kt:21-22`); `random(includeFaith)` returns one (`:81`); spiritual lines filtered out unless `includeFaith` (`:77-78`); builds display citation strings (`:32-36`, `:57-64`).
- `app/src/main/java/.../model/Affirmation.kt` — data class `{ id, text, pillar, citation, scriptureText? }`, `isFaith` helper.
- `app/src/main/java/.../ui/screens/DashboardScreen.kt` — `AffirmationBanner` (`:349`) rendered on Home after check-ins (`:241-246`); tap-to-reroll (`:244,351`); bell icon (`:359`).
- `app/src/main/assets/affirmations_recovery.json`, `affirmations_spiritual.json` — bundled copies of the `content/` files.

---

## Decision Record

**1. Authored, not quoted — but always derived and always referenced.**
- Alternatives: (a) pure sourceless original affirmations ("you are strong"); (b) verbatim quotes of the source.
- Reason: explicit user directive — "you have creative freedom to interpret and author your own words but derived from the source and referenced." Creative freedom in voice; fidelity in derivation. A citation is a truth claim, so each authored line must genuinely say what the cited passage says.

**2. Two files split by pillar (recovery / spiritual), not one combined file.**
- Alternative: a single `affirmations.json`.
- Reason: mirrors the existing content split (secular `passages_core.json` vs faith `clearStreak-daily-verse.json.json`) and matches the app's faith-gating. Confirmed correct: `AffirmationStore.kt` loads two named assets and filters spiritual by the faith toggle.
- Note: `PROGRESS.md:28` still describes a single placeholder `assets/affirmations.json`; reality is now two pillar files with real content (see Living Spec update below).

**3. Recovery citations reuse provenance already verified in `passages_core.json`.**
- Alternative: derive fresh page/paragraph numbers from `big_book_index.json`.
- Reason: guarantees byte-accurate `page` / `paragraph_index` / `quote_stub` and avoids off-by-one page errors (the passages report noted 4 such corrections already made). Extracted the exact source blocks via PowerShell before authoring.

**4. Spiritual `verse_text` is exact KJV; scope is the whole KJV, not just the daily-verse categories.**
- Alternative: limit to the 3 existing daily-verse categories (anxiety / peace / presence).
- Reason: broader poetic range. KJV is unambiguously public domain, so no IP concern.

**5. Affirmations carry no surfacing labels (abstract / universal).**
- Alternative: tag each with `moment` / `halt` / `urge_level` for state-based routing like the passages.
- Reason: user framing — affirmations "only have their own context." They are the always-safe layer that can surface anytime. Validated by the consumer: the Home banner needs only `text` + `citation`, nothing to route on.

**6. No review gates at all — removed `needs_clinical_review`, then `needs_theological_review`.**
- Alternative: keep a clinical gate on recovery and a pastoral/theological gate on spiritual (both were initially written in).
- Reason: two explicit user directives — "nothing we do needs clinical review" and "we need no pastoral check — you are the author." This is original 864zeros-authored content; the author is the reviewer. Aligns with the existing Blueprint decision (`ClearStreak_Blueprint_v1.md:121`, "No IP/legal gate on re-authored content").

**7. 50 affirmations per pillar (100 total) for v1.**
- Alternative: a larger initial set.
- Reason: user said "use your best defaults." Easily expanded later; the schema and style note make additions mechanical.

---

## Deferred Items (new this session)
- **Reviewer-facing CSV/HTML export** for affirmations (authored line ↔ source side-by-side). *Why deferred:* offered but not requested, and with review gates removed the reviewer artifact has no consumer. *Pick up when:* someone wants a human proofread pass.
- **Expanding affirmation counts beyond 50/pillar.** *Why deferred:* 50/50 was the agreed v1 default. *Pick up when:* product wants more variety in the Home banner rotation.
- **Stripping `needs_clinical_review: true` from the older `passages_core.json` (130 records) + `PASSAGES_REPORT.md`.** *Why deferred:* the no-gate directive was stated for new work; user was asked whether to retro-strip the old files and has not answered. *Pick up when:* user confirms. (See memory `no-clinical-review.md`.)

---

## In-Progress Work
- **Asset-sync verification — UNVERIFIED.** The content files (`content/affirmations_*.json`) and the app's bundled copies (`app/src/main/assets/affirmations_*.json`) are **separate copies**. A `cmp` check to confirm they are byte-identical was started but the tool call was rejected/interrupted before it ran, then `/doc` was invoked. *Next session must:* run `cmp -s` (or equivalent) on both pairs and re-copy content→assets if they differ, since the app ships the assets copy, not `content/`.

---

## Next Session Primer
- **Content authoring vs. app bundle are two locations.** Author in `content/`; the app ships copies in `app/src/main/assets/`. Keep them in sync — this is the top open item (see In-Progress).
- **Consumer entry points:** `data/AffirmationStore.kt` (loading + faith filter + random) and `ui/screens/DashboardScreen.kt` (`AffirmationBanner`, Home). Touch these if changing how affirmations surface.
- **No review gates on any ClearStreak content** (memory rule `no-clinical-review.md`). Do not reintroduce `needs_clinical_review` / `needs_theological_review`.
- **Provenance is the only standard that remains:** recovery = real Big Book page/paragraph_index/quote_stub (reuse `passages_core.json`); spiritual = exact KJV `verse_text`.
- **`PROGRESS.md:28` was stale** on the affirmations line (said single placeholder file) — updated this session; if it reads otherwise, re-check.

---

## Housekeeping notes
- This repo has **no `IGNORE/` directory and no backlog/BTW files** (`DEFERRED_TOPICS.md`, `BTW_LOG.md` all absent). Session snapshots live at repo root (e.g. `SESSION_SUMMARY_2026-08-19_1458.md`). Steps 1 and 4 of `/doc` (backlog reconcile) had nothing to act on.

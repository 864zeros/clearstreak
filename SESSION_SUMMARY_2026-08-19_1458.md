# ClearStreak — Session Handoff Summary
**Generated:** 2026-08-19 14:58 · **Author:** Claude (Opus 4.8) session
**Purpose:** Full context for any human or LLM CLI to understand the project and continue work after reading this one file.

---

## 0. TL;DR (read this first)

ClearStreak is a **privacy-first Android recovery app** (864zeros). This session built a **literature content pipeline**: it extracted the public-domain **1939 first-edition Big Book** and mined its most useful passages into **130 modernized, 2026-appropriate help "passages"** for two uses — an in-app **book reader** and **moment-of-need surfacing** (triggered by the user's urge tier + HALT state).

- **The deliverable:** `content/passages_core.json` (130 passages, validated, 0 outstanding issues).
- **Status:** Draft content complete. Every passage is flagged `needs_clinical_review: true` — **nothing ships without a licensed (LCSW/CADC) review.**
- **Recommended next step:** (C) build a clinical-review handoff export, then (B) wire into the app.
- **Full detail report:** `content/PASSAGES_REPORT.md` (and `.html`).

---

## 1. Project context

- **Product:** ClearStreak — local-first, zero-knowledge sobriety/recovery companion. No accounts, no cloud, no telemetry. Encrypted on-device journal (SQLCipher), biometric gate, multi-journey streak tracker, 4-tier urge pulse + HALT triggers, crisis intercept, deterministic coping cards. One-time $4.99 IAP.
- **Org:** 864zeros LLC · pillar "Faith, Health & Growth."
- **Platform:** Android — Kotlin + Jetpack Compose, Gradle KTS. Package `com.eight64zeros.clearstreak`.
- **Repo root:** `C:\dev\clearStreak` (NOTE: on `C:\dev`, **not** under the user home dir).
- **Key spec docs:** `ClearStreak_Spec_v2.md`, `ClearStreak_Blueprint_v1.md`, `PROGRESS.md`, `README.md`.
- **Spec constraint that shaped this work (§9.1):** content must be "100% original or properly licensed"; AA literature was originally *excluded* pending license. This session's re-authoring approach satisfies that (see §3).

---

## 2. What this session accomplished

1. **Extracted** the 1939 Big Book EPUB → `content/big_book.json` (42 sections, 1,453 paragraphs, front/main/stories). Deliberately excluded Dover's modern (copyrighted) additions — kept only the 1939 public-domain text.
2. **Built a page-provenance index** → `content/big_book_index.json` (page number per paragraph; 396 page anchors).
3. **Defined a content taxonomy** → `content/taxonomy.json` (label vocabulary + state→content routing).
4. **Mined + modernized** Tier 1 (full) and Tier 2 (selective) chapters via 12 parallel subagents, each following a shared style guide, then QA-merged → `content/passages_core.json` (130 passages).
5. **Wrote the detail report** → `content/PASSAGES_REPORT.md` + `.html` (864zeros design system).

---

## 3. THE CONTRACT (non-negotiables — do not violate when continuing)

These were explicitly decided with the operator. Any further content work must honor them:

1. **Technique-only + optional faith.** Keep universally-safe, evidence-aligned techniques (urge-surfing, HALT, delay, expressive writing, reach-out). **Re-map AA doctrine into our framework — do NOT delete it, do NOT preach it.** (Mapping table in `PASSAGES_REPORT.md` §2 and `_src/STYLE_GUIDE.md`.)
2. **Fidelity over brevity.** NEVER abbreviate/compress the message to fit a UI shape. `reading_time` is metadata only. *(The operator interrupted an earlier flow the moment content-length limits entered the reasoning — this is a hard rule. Saved to memory: `clearstreak-fidelity-over-brevity`.)*
3. **The Twelve Steps are untouchable.** Never mined, reworded, or labeled. Fenced at `ch05` paragraphs **6–17**. Verbatim if ever displayed. (1939 Step 12 = "spiritual experience," not "awakening" — keep it.)
4. **Two renderings per passage:**
   - `reader_text` = **light touch** — keep the 1939 voice; change only dated/exclusionary words; re-map doctrine; move faith lines to `faith_optional`. (Used in the book reader.)
   - `surface_text` = **re-voiced** — plain, warm 2026 language; same complete meaning. (Used in moment-of-need.)
5. **Faith is opt-in.** Zero God/Higher-Power references in `reader_text`/`surface_text`; all faith content lives in `faith_optional`, shown only if the user enables faith mode.
6. **Gender-neutral, no moralizing** (no sin/defect/insanity/moral-failure/"you must"; "broken" only in a negated/anti-shame sense).
7. **Provenance is exact.** `source.page` + `source.paragraph_index` + `source.quote_stub` must match `big_book_index.json` / `big_book.json`. Never invent.
8. **Everything is `needs_clinical_review: true`** until a licensed counselor signs off.
9. **Scope for v1:** the 29 personal stories are **dropped** (rewriting testimony destroys authenticity; revisit with real modern voices later). The `persona` axis was removed with them.
10. **IP:** the 1939 first-edition public-domain basis is real (Dover reprint) but **disputed by AA World Services**. Re-authoring reduces exposure. A definitive legal read is still owed **before the store build ships** this content.

---

## 4. File map (everything this session touched)

```
C:\dev\clearStreak\
├─ SESSION_SUMMARY_2026-08-19_1458.md   ← THIS FILE
├─ ClearStreak_Spec_v2.md               ← product spec (read §9 content strategy)
├─ PROGRESS.md, README.md, *_Blueprint_v1.md
├─ app\src\main\
│  ├─ assets\
│  │  ├─ daily_verses.json              ← existing content-load pattern to mirror
│  │  └─ big_book.json                  ← full reader text (copied here for the app)
│  └─ java\com\eight64zeros\clearstreak\
│     ├─ model\Enums.kt                 ← UrgeLevel, HaltTrigger(+GENERAL), JourneyCategory
│     ├─ model\ (CheckIn, Journey, DailyVerse, Milestone, StreakStats)
│     └─ data\ (HeritageStore, *Storage, StreakCalculator, HapticEngine)
└─ content\
   ├─ passages_core.json                ← ★ THE DELIVERABLE (130 passages)
   ├─ taxonomy.json                     ← controlled vocab + routing (source of truth)
   ├─ big_book.json                     ← full 42-section reader text
   ├─ big_book_index.json               ← page-per-paragraph provenance
   ├─ PASSAGES_REPORT.md / .html        ← detailed report (864zeros design)
   ├─ extract_bigbook.ps1               ← re-runnable EPUB extractor
   ├─ aa-bigbook-1939-full-PD.epub      ← source (Dover 1939 reprint, PD basis)
   ├─ passages_sample.json              ← 3-passage calibration file (SUPERSEDED — deletable)
   ├─ clearStreak-daily-verse.json.json ← (unrelated existing content)
   └─ _src\                             ← mining workspace (reproducibility)
      ├─ STYLE_GUIDE.md                 ← ★ the rulebook every mining agent followed
      ├─ <chapter_id>.json              ← per-chapter source w/ exact {i,page,text}
      └─ out_<chapter_id>.json          ← per-chapter mined drafts (pre-merge)
```

---

## 5. Content model & taxonomy

**Two layers, one vocabulary** (`taxonomy.json`):
- **Reader (Layer 1):** `big_book.json` — 42 ordered sections. Full read.
- **Passages (Layer 2):** `passages_core.json` — mined units that point back to the reader via `source{chapter_id,page,paragraph_index}`.

**Passage record:**
```json
{
  "id": "lit_ch05_7",
  "reader_text": "…", "surface_text": "…", "faith_optional": null,
  "labels": { "moment": ["resentment","fear"], "halt": "GENERAL",
              "urge_level": "WHITE_KNUCKLING", "stage": "action", "function": "instructional" },
  "applies_to": ["UNIVERSAL"],
  "framework_map": "…which doctrine re-mapped…",
  "reading_time": "medium",
  "source": { "book":"AA-1939","chapter_id":"ch05","chapter_title":"V. How It Works",
              "page":76,"paragraph_index":36,"quote_stub":"Resentment is the …" },
  "needs_clinical_review": true
}
```

**Label axes & allowed values:**
- `halt` (code enum `HaltTrigger`): HUNGRY, ANGRY, LONELY, TIRED, STRESSED, HOPELESS, GENERAL
- `urge_level` (code enum `UrgeLevel`): CLEAR, PASSING, WHITE_KNUCKLING, CRITICAL
- `applies_to` (code enum `JourneyCategory` + flag): UNIVERSAL, ALCOHOL, DRUGS, VAPE, GAMBLING, BEHAVIORAL, CUSTOM
- `moment` (PRIMARY ROUTER, content-native): starting-out, is-this-me, craving-now, after-a-slip, doubt-higher-power, resentment, fear, lonely, family-strain, work-strain, helping-others, staying-the-course, hopelessness
- `stage`: contemplation, decision, action, maintenance, relapse-recovery
- `function`: foundational, instructional, identification, relational, spiritual
- `reading_time`: quick, medium, long (surfacing hint only)

**Routing (live app state → content):** `moment` is derived from urge tier × HALT tap. Full map in `taxonomy.json` `routing`. CRITICAL routes to Crisis Intercept first; a `quick` grounding passage is optional-secondary.

---

## 6. Current state

- 130 passages, validated: **0 outstanding issues** (label enums valid, provenance verified, 0 Steps leaks, 0 faith leaks in user-facing text).
- Distribution: 31/130 have a faith layer; 125 UNIVERSAL / 5 alcohol-specific. Moment/urge/HALT spreads are healthy (see report §3). HUNGRY=0 is expected (book doesn't address hunger).
- Known soft spot: **ch01 (18) and ch02 (23) are over-mined** vs other chapters — candidate for a trim/rank pass.

---

## 7. Open decisions & next steps

Pick one (recommended order **C → B**):
- **(A) Trim / rank** ch01–ch02 over-mining into a tighter curated set.
- **(B) Wire into the app** — author a `BookPassage` (+ `BookChapter`) Kotlin model and a loader mirroring how `daily_verses.json` is read; build a first reader + moment-of-need surfacing screen. Load `big_book.json` for the reader and `passages_core.json` for surfacing.
- **(C) Clinical-review handoff** — export a reviewer-friendly view (original ↔ `reader_text` ↔ `surface_text` + provenance) as HTML/CSV for passage-by-passage LCSW/CADC sign-off. **Do this before B** so UI isn't built on unreviewed content.

Still owed regardless: **final legal read on the 1939 PD status** before the store build ships this content.

---

## 8. How to continue (concrete first actions for the next agent)

1. **Read, in order:** this file → `content/PASSAGES_REPORT.md` → `content/taxonomy.json` → `content/_src/STYLE_GUIDE.md`.
2. **Re-validate the deliverable** (PowerShell) before changing anything:
   ```powershell
   $doc = Get-Content "C:\dev\clearStreak\content\passages_core.json" -Raw -Encoding UTF8 | ConvertFrom-Json
   $P = $doc.passages
   $n=0; foreach($p in $P){$n++}; "passages: $n"   # expect 130
   ```
3. **If mining more content**, reuse the pipeline: per-chapter source files in `_src/<id>.json`, the `STYLE_GUIDE.md` rulebook, one subagent per chapter, then a QA-merge that checks label enums + provenance + Steps fence. (The QA/merge logic is described in the report and was run inline this session.)
4. **If editing passages_core.json directly**, keep it valid JSON, write UTF-8 **no BOM**, and preserve the contract (§3).

---

## 9. Environment gotchas (learned this session)

- **Use PowerShell, not Bash.** The Bash tool had path resolution problems on this Windows box (`C:\dev` is not under the home dir; `/c/dev/*/` globbing failed). PowerShell 5.1 (`powershell.exe`) is the reliable shell here.
- **PowerShell 5.1 array quirk:** member enumeration on JSON arrays behaved inconsistently (`$P.Count`, `$P.labels.moment` sometimes returned wrong/empty results, and `Where-Object` intermittently found nothing). **Aggregate with explicit `foreach` loops**, not pipeline member-access.
- **Write JSON without BOM:** `[System.IO.File]::WriteAllText($path,$json,(New-Object System.Text.UTF8Encoding($false)))`. `Out-File -Encoding utf8` adds a BOM in 5.1.
- **Paragraph indices align:** `big_book.json` and `big_book_index.json` share identical paragraph ordering — paragraph `i` maps 1:1 across both (and into `passages_core.json` `source.paragraph_index`).
- **Page anchors:** ~1 per printed page (396 total), but many sit *mid-paragraph*; deriving page-per-paragraph required replacing `<a id="page_N"/>` with sentinels before splitting. `extract_bigbook.ps1` and the index builder handle this.
- **Steps fence:** `ch05` `no_touch` = paragraphs 6–17. Any mining tool must skip that range.

---

## 10. Memory & related context

Relevant persistent memories (in `C:\Users\Jeff\.claude\projects\C--Users-Jeff-dev\memory\`):
- `clearstreak-fidelity-over-brevity` — the hard rule from §3.2.
- `android-build-kit-goal` — ClearStreak is the reference impl for a repeatable 864zeros Android scaffold.
- `MEMORY.md` — index of all memories.

---
*864zeros LLC · ClearStreak · handoff generated 2026-08-19 14:58 · code wins.*

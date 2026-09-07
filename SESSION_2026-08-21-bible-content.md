# Session Snapshot — 2026-08-21 — Bible scripture content library

## Session Header
- **Topic:** Building a complete public-domain Bible key-verse library (scripture + original plain-English renderings) in `content/`, as reusable 864zeros ecosystem content. (Work spanned 2026-08-20 evening → 2026-08-21.)
- **State at start:** `content/` had finished ClearStreak content (daily verses, 130 Big Book passages, 100 affirmations). The directory's mission had just been redefined (by the user) as a **PD Bible/spiritual content factory for the whole 864zeros ecosystem** — ClearStreak content itself considered complete. A `proverbs_daily.json` had just been started.
- **State at end:** The **entire Bible** exists as key-verse JSON — all 66 books, **834 records**, each carrying verbatim PD scripture plus an original 864zeros plain rendering; every file validated clean (parses, meta counts match, no curly quotes, no empty fields).

---

## What Was Built

Context for a zero-context reader: `content/` (under repo `C:\dev\clearStreak\`) is a **content factory**. It produces JSON in the conventions established for the ClearStreak recovery app, but the Bible content here is **ecosystem-generic** — any 864zeros app can ingest it. Nothing here is wired to a single app.

**Every scripture record uses this schema:**
```json
{ "book": "...", "chapter": N, "citation": "Book C:V", "text": "<verbatim PD scripture>", "plain": "<original 864zeros 2026 rendering>" }
```
`text` = the exact public-domain verse. `plain` = an original 864zeros paraphrase (~grade 6–8) — this is the *product*, the differentiator. Files carry a `_meta` block (title, translation, license, source, selection policy, count).

### The three "special-shape" sets
- **`content/proverbs_daily.json`** — 31 records, keyed `day` 1–31 → Proverbs chapter 1–31 (Proverb-a-Day). Anchor: **WEB** (World English Bible), divine name rendered "the Lord". Has `day`, `chapter`, `citation`, `text`, `plain`.
- **`content/psalms_daily.json`** — 150 records, one curated verse per Psalm, keyed by `psalm`. Anchor: **WEB** → "the Lord"/"Lord" (vocative). Verses fetched, then divine name adapted; "Selah" dropped.
- **`content/gospels_daily.json`** — 89 records, **one verse per chapter** across Matthew/Mark/Luke/John. Anchor: **KJV**. Keyed by `book`+`chapter`.

### The per-book "several key verses per chapter" files (KJV anchor)
- **23 NT books** — `acts.json`, `romans.json`, `1corinthians.json` … `revelation.json` — **357 records** total.
- **37 OT books** — `genesis.json`, `exodus.json` … `malachi.json` (Psalms/Proverbs excluded — they are the `_daily` files) — **207 records** total.
- Each has multiple records per substantive chapter, `_meta.count` = record count.

### Non-obvious implementation details
- **Sourcing:** scripture text pulled from **getbible.net v2** as whole-book JSON via `curl` (KJV book numbers 1–66; e.g. Genesis=1, Matthew=40, Revelation=66), then verses selected/validated locally in PowerShell. Cross-checked against the local `content/The-Holy-Bible-King-James-Version.pdf` (via `pdftotext`) for the Gospels — John 3:16, "I will give you rest", John 8:32 matched verbatim.
- **Divine-name handling:** getbible's **KJV renders the tetragrammaton as "the Lord"** (mixed case, not small-caps LORD), matching our style — no adaptation needed for KJV. WEB uses "Yahweh"/"Yah", which we adapted → "the Lord" (subject/possessive) and "Lord" (vocative address).
- **Text normalization:** curly quotes/apostrophes → straight ASCII; proper-name en-dashes → hyphens (e.g. "Jehovah-jireh", "Eben-ezer"); whole-verse wrapping parentheses, editorial colophons (2 Cor 13:14), and "Selah" markers dropped for clean standalone display. Files written UTF-8 no-BOM.
- **OT chapter coverage:** for narrative OT books, **pure-genealogy and graphic-only chapters are intentionally skipped** (they have no devotional verse), so coverage is "most chapters," documented in each `_meta.selection`. NT epistle books cover 100% of chapters.

---

## Decision Record

**1. `content/` is a PD Bible/spiritual content factory; ClearStreak content is complete.**
- Alternative: keep producing ClearStreak-specific content.
- Reason: explicit user directive — "the only purpose this content directory has is to create content in the format we used for clearStreak… we are complete [on ClearStreak]… using new sources (only PD versions) to create many more bible/spiritual content… digested across all of 864zeros ecosystem." Saved to memory (`content-factory-mission.md`).

**2. `plain` is an ORIGINAL 864zeros paraphrase — NOT a published simple translation.**
- Alternative: use the local `BBE--Bible_in_Basic_English.pdf` (Bible in Basic English, a real PD simple-English translation) as the "plain" layer.
- Reason: the user's Socratic correction — "why do you think we asked you to do the paraphrasing to begin with?" The paraphrase **is the product**: original, owned 864zeros IP in a fresh 2026 voice. BBE is third-party, 1940s-dated, and non-differentiated (any app could ship the identical text). This was the single most important course-correction of the session.
- Constraint: BBE is a PDF (error-prone to parse per-verse); clean JSON is easier — but that was never a reason to *use* BBE as content.

**3. Keep the exact scripture (`text`) alongside the paraphrase (`plain`) — dual rendering, not replacement.**
- Alternative: replace the verse with the modern paraphrase and keep only one text.
- Reason: provenance honesty — a paraphrase labeled "Proverbs 3:5" would misrepresent if it *were* the only text; the citation is a truth claim, so the actual verse must sit beside our authored line. Mirrors the Big Book passages' two-rendering pattern (`reader_text`/`surface_text`).

**4. KJV anchor for the NT and most of the OT; WEB for Psalms & Proverbs.**
- Alternative: one uniform translation across the whole library.
- Reason: WEB was chosen first (via an early translation question) for Proverbs/Psalms, before the user later chose **KJV** as the anchor ("KJV anchor, go ahead"). KJV matches the app's existing `daily_verses.json` and is cross-checkable against the local KJV PDF. This leaves a **known, deliberate inconsistency** (Psalms/Proverbs = WEB; rest = KJV); the user was told and opted not to re-anchor. Flagged as easy to fix later.

**5. Sourcing via getbible.net whole-book bulk `curl`, not per-verse API calls.**
- Alternative: fetch each verse from bible-api.com; or parse the local PDFs.
- Reason: bible-api.com **rate-limited (HTTP 429)** at ~16 requests per short window during the Psalms fetch. One `curl` of a getbible book returns the entire book as clean JSON — no rate limit, exact structure. PDF parsing is error-prone for verse boundaries. Local KJV PDF kept as cross-check source-of-record.

**6. "Several key verses per chapter" density for Acts→Revelation and the OT.**
- Alternatives considered (I laid out three products for the user): (a) one flagship verse per chapter (what the Gospels use, ~89 NT records); (b) several key verses per chapter; (c) every verse (~7,957 NT).
- Reason: user explicitly chose (b) after I clarified the difference — richer than a daily-surfacing set, not full-text. Acts was built as the "pilot" to confirm density (~2.5 verses/chapter) before committing the rest.

**7. No review gates on any of this content (author is reviewer).**
- Alternative: clinical/theological review flags.
- Reason: established earlier in the session — "nothing we do needs clinical review"; "we need no pastoral check — you are the author." See memory `no-clinical-review.md`.

**8. Content-filter finding (`400 Output blocked by content filtering policy`).**
- Decision/finding, not a build: the error the user reported is **Anthropic's API-layer output filter**, firing in the *user's* pipeline (not this Claude Code session — all files wrote fine). Likely trigger: **Revelation's graphic apocalyptic imagery**. Our `plain` softening does NOT neutralize it, because `text` keeps the raw KJV verbatim. Mitigation: ship as **static JSON at runtime** (no live API call → filter never fires); don't round-trip the raw verses through the API.

---

## Deferred Items (new this session)
- **Re-anchor Psalms & Proverbs from WEB to KJV** — for one uniform translation. *Deferred:* user was offered and opted not to; it's cosmetic. *Pick up when:* user wants translation uniformity.
- **Combined manifest / index (`bible_index.json`)** listing all 66 book files for single-entry-point loading. *Deferred:* offered, not requested. *Pick up when:* an app needs to enumerate the library programmatically.
- **Density expansion of the OT narrative books** — they were kept intentionally lean (1–3 verses/chapter, high points only). *Pick up when:* a use case needs richer OT coverage.
- **Per-book "10 Life Lessons" study sets** — the other content class the local `Bible {prompts} v1.docx` template (Biblical Scholar persona → book overview + 10 lessons) was built for. *Deferred:* different product; never started. *Pick up when:* study/devotional content is wanted beyond verse lists.
- **Content-filter root-cause pinpointing** — offered to scan the generated files for the exact high-risk trigger phrases. *Deferred:* waiting on the user to say *where* the 400 fires (app runtime vs. generation pipeline).

---

## In-Progress Work
None. The Bible key-verse library is **complete and validated** (63 scripture files, 834 records, all clean). One naming note: getbible titles book 22 "Song of Songs"; the file uses `Song of Solomon` (KJV title) in both filename (`songofsolomon.json`) and `book` field.

---

## Next Session Primer
- **`content/` is a PD Bible/spiritual content factory for the whole 864zeros ecosystem** (memory: `content-factory-mission.md`); ClearStreak content is done — don't add more for ClearStreak.
- **Scripture schema:** `{ book, chapter, citation, text, plain }`. `text` = verbatim PD scripture; `plain` = original 864zeros writing (the value). **No review gates.** Keep `plain` original — do NOT substitute a published translation like BBE (see Decision 2).
- **Sourcing recipe:** `curl https://api.getbible.net/v2/kjv/<n>.json` (n = book number 1–66) → whole book as clean JSON → select verses in PowerShell → cross-check vs local `content/The-Holy-Bible-King-James-Version.pdf`. bible-api.com rate-limits (~16/window); prefer bulk getbible.
- **Known state:** Psalms/Proverbs use **WEB** anchor ("the Lord"); everything else **KJV**. `gospels_daily.json` = one verse per chapter; all other NT/OT per-book files = several key verses per chapter.
- **Content-filter gotcha:** the `400 Output blocked by content filtering policy` is an Anthropic **output filter** in the *user's* pipeline (Revelation's imagery is the likely trigger). Shipping this as static JSON assets avoids it entirely — do not re-send raw scripture through a live API call.

---

## Housekeeping
- Repo still has **no `IGNORE/` directory and no backlog/BTW files** (`DEFERRED_TOPICS.md`, `BTW_LOG.md` absent). `/doc` Steps 1 and 4 had nothing to reconcile. Session snapshots live at repo root (`SESSION_2026-08-20-affirmations.md`, `SESSION_SUMMARY_2026-08-19_1458.md`).
- A living index/spec for the library was written this session: `content/BIBLE_LIBRARY.md`.

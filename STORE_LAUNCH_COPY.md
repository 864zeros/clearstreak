# ClearStreak — Play Store Listing Copy + Data Safety Answers

*Launch pack. Copy fields verbatim into the Play Console. All claims verified against the app's
actual behavior. **No treatment/cure/medical claims** — framed as a personal companion + tracker
to avoid Play's health medical-claims rejection.*

---

## 1. Store listing

### App name (max 30 chars)
**`ClearStreak: Private Habits`**  *(28 chars)*

> Alternatives if you prefer: `ClearStreak: Habit Companion` (29) · `ClearStreak` (plain, 11).

### Short description (max 80 chars)
**`Private habit & vice companion. Data over shame. Encrypted, offline, no cloud.`**  *(78 chars)*

> Alt: `The private habit & vice companion — encrypted, offline, and yours alone.` (72)

### Full description (max 4000 chars — this is ~2,650)

```
Your private habits are yours alone.

ClearStreak is a private, offline habit and vice companion for staying on track — built on
the opposite premise from most wellness apps. There is no account, no cloud, and no
tracking. Everything you track stays encrypted on your own phone, where only you can reach it.

DATA OVER SHAME
• Data over shame: an off day resets today's streak count, but never wipes your history.
• Your cumulative progress, past stretches, and personal bests are always preserved.
• Permanent milestone markers and an honest calendar make your progress visible and earned.
• The language never shames you or says "failure."

PRIVATE BY ARCHITECTURE
• Your notes and check-ins are stored in an AES-256 encrypted database on your device.
• The encrypted data is unlocked with your fingerprint, face, or device PIN/pattern.
• No account. No sign-in. No server. Nothing to breach, sell, or subpoena — because your
  data never leaves your phone.
• No analytics, no trackers, no ads.
• Screenshots and screen-recording are blocked, and the app hides its contents in the app
  switcher, so nothing flashes on screen by accident.

TRACK EVERY JOURNEY
• Follow multiple habits and vices at once — vaping, alcohol, smoking, doomscrolling,
  gambling, emotional eating, late-night spending, or a custom goal — each with its own
  start date, milestones, and money saved.
• A quick, honest daily check-in with optional HALT context (Hungry, Angry, Lonely, Tired).
• A home-screen widget keeps your current streak and next milestone in view.

TOOLS FOR THE HARD MOMENTS
• A one-tap Rescue hub puts your trusted support person and confidential national lifelines
  (the 988 Suicide & Crisis Lifeline, SAMHSA, Crisis Text Line, and specialized quit-lines)
  within reach.
• Eyes-free grounding tools: a paced breathing circle with gentle haptics, a grounding
  timer, and calming tactile sessions to help an urge pass without reacting.
• Optional mind-occupying tactile sessions for distraction.

REFLECT, IF YOU WANT IT
• An optional, fully offline library of short modern reflections and timeless wisdom,
  browsable by theme — always available, never forced.

HOW WE MAKE MONEY
• A 7-day free trial, then a one-time unlock. No subscription. No ads. No selling your data —
  ever. We literally cannot see your personal data, so we could never sell it.

The crisis Rescue hub stays reachable even before you unlock.

ClearStreak is a supportive tool and personal tracker. It is not a medical device, not
therapy, and not an emergency service. If you are in crisis, call your local emergency
number or the 988 Suicide & Crisis Lifeline.

864zeros LLC — local-first software. Private by architecture. Offline by default.
```

### Other listing fields
- **App category:** Health & Fitness *(NOT "Medical" — avoids the stricter medical-app review track)*.
- **Tags:** sobriety, recovery, habit tracker, privacy, offline.
- **Privacy Policy URL:** `https://www.864zeros.com/privacy/clearstreak` *(host `PRIVACY_ClearStreak.md` there — confirm final URL)*.
- **Contact email:** jeff.m.conn@gmail.com  ·  **Website:** https://www.864zeros.com
- **Graphics still needed (you):** app icon (512×512), feature graphic (1024×500), ≥2 phone
  screenshots (use the `-Pcapture` debug build to shoot them — screenshots are `FLAG_SECURE`-blocked otherwise).

---

## 2. Data Safety form (exact answers)

Google's Data Safety = **only data that leaves the device** counts as "collected/shared."
ClearStreak transmits **no** user data off the device. Google Play Billing is handled by Google
and is **exempt** from your declaration (per Play's own guidance — purchases processed by Google
Play don't need to be declared as your collection). So:

### Data collection & sharing
- **Does your app collect or share any of the required user data types?** → **No.**
  - Rationale: journeys, check-ins, journal notes, streaks, and contacts are stored **only on
    the device**. Nothing is transmitted to you or any third party. The store build's INTERNET
    permission is used **solely** for Google's purchase check; the app sends no user data over it.

*(Answering "No" collapses the per-data-type table — you won't need to declare Health, Financial,
Personal, or any other category.)*

### Security practices (still asked even when nothing is collected)
- **Is your data encrypted in transit?** → Not applicable / No data is transmitted off the device.
  *(If forced to pick: the app transmits no user data; on-device data is encrypted at rest with
  AES-256.)*
- **Do you provide a way for users to request that their data be deleted?** → **Yes — users delete
  data in-app or by uninstalling; all data is local, and there is no server copy or account.**

### Additional flags
- **Does your app handle sensitive user data (e.g., health)?** The data is health-related but is
  **stored on-device only and never collected by you**, so it is **not declared as collected**.
  If a reviewer asks, the honest statement: *health information is entered by the user and stored
  locally in an encrypted database; it is never transmitted or shared.*
- **Account creation:** None. No account deletion flow required.

---

## 3. Adjacent forms you'll hit in the same flow (heads-up)

- **Content rating (IARC questionnaire):** Choose **Utility / Reference / Other** or **Health**;
  answer **No** to violence, sexual content, gambling *(the mini-games have zero gambling
  mechanics — no reels, wagering, or simulated gambling; answer No)*, and profanity. Note it
  **references alcohol/drugs in a recovery/educational context** (not glamorized). Likely rating: Teen/Everyone.
- **Target audience & content:** Target **18+ / Adults**. The app is **not** designed for children.
- **Health apps declaration:** If Play surfaces a Health Apps form, declare it a **wellness /
  self-management** tool, **not** a medical device, **no** clinical/diagnostic claims, and **no**
  Health Connect usage.
- **Ads:** Declare **contains no ads**.
- **Government/financial/health restricted categories:** none apply.

---

*Every claim here matches the code as of `main`. Re-verify the Privacy Policy URL before submitting.*

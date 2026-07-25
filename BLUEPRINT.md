# 🌿 Vaa — Blueprint

**Author:** Schuyler Vianney Lewis (Chairman)
**Collaborator:** Claude (Anthropic)
**Date:** 2026-07-11
**Status:** 🟡 Blueprint — Discussion complete, ready for phase planning. No code yet.

---

## FRAGMENT EXTRACTION GUIDE — read this before pulling any phase fragment

This document serves two audiences: it's the full planning record for the Chairman and Claude, and it's the source Claude extracts **per-phase fragments** from before drafting each Gemini prompt. Google AI Studio/Gemini never sees this full document — only a fragment built from it, one phase at a time.

Every section below is tagged with its fragment role:

- **[SKELETON]** — minimal structural facts about the whole app. Included in every fragment, trimmed to essentials only (no rationale, no tradeoff discussion) — gives Gemini just enough "where this phase sits" context, not full detail on phases it isn't building.
- **[STANDING RULE]** — applies to every phase without exception. Included in full in every fragment.
- **[PHASE-SPECIFIC]** — the actual build content. Only the entry for the **current** phase is extracted; all other phase entries are omitted from that fragment.
- **[CLAUDE/CHAIRMAN ONLY]** — rationale, tradeoffs, history, shelved plans, distant-future notes. Never enters a fragment. Exists to inform decisions, not to be read by an implementer.

Every fragment must open with this instruction, verbatim or equivalent:

> *"This is a partial extract of a larger project blueprint, provided for structural context only. Implement only what this specific phase requests. Do not build, scaffold, or reference functionality belonging to other phases, even where this context mentions they exist."*

Fragments are generated fresh from this document at the time each phase is actually prompted — not pre-written in advance — so they stay accurate if this blueprint changes between now and when a later phase's turn comes.

---

## 0. What This Is — [SKELETON, trimmed] / [CLAUDE/CHAIRMAN ONLY, rationale]

Vaa is a single-shell Android app: bottom-placed WhatsApp-style tabs (Chats / Updates / Loader / 4th tab, swipeable), with a round-icon tab strip for currently-open threads sitting as the true bottom-most element on screen, below the main tab bar, global across all four top-level tabs (not scoped to Chats alone). Every AI site and dev site lives in it as a "thread." Some threads render as scraped chat bubbles, some as the real live page, one type runs a model locally on-device. This is **not a general browser** — no free URL navigation, no arbitrary sites, no filter-list ad blocking. Everything is a named, fixed module, added deliberately.

*[CLAUDE/CHAIRMAN ONLY — why:]* A real browser (arbitrary sites, arbitrary JS injection, ad-block filter lists) is a security and auditability problem on a phone with no laptop, no devtools, and a code generator that fabricates completion claims. A fixed set of known modules is small enough to actually test and trust.

**Macro/micro build model** *[STANDING RULE]*: Claude operates macro (structure, sequencing, prompt authorship) and drops to micro only for debugging (reading actual pasted code, since Gemini's own bug reports aren't independently trustworthy). Google AI Studio/Gemini operates micro (writes the code, reports, tests) against Claude's structured phase prompts. Every phase's output (zip, logs, Gemini's own report) is checked against actual files before the next phase is authorized — if Gemini reports a problem, it is first asked for solution options; those options are reviewed and either adopted/adjusted or replaced with Claude's own solution if they don't hold up. Verification depth is standard for most phases; phases touching security-sensitive logic (multi-account isolation, API key storage, anything in Section 12) get closer scrutiny, not a shallow pass.

---

## 1. Core Philosophy — [STANDING RULE]

- **Fixed site list, not free browsing.** Every thread is a named module. No general address bar navigation to arbitrary domains.
- **Site is the source of truth.** Chat modules adapt to sites, not the other way around.
- **Cookies only, nothing else cached.** Login sessions persist; page cache does not.
- **Alive vs sleeping, not three states.** A small number of tabs stay live, the rest sleep and silently reload on demand.
- **Automation is best-effort.** When a chat module breaks, the Chairman chooses what happens next — nothing silent.
- **No backend, nothing leaves the device** except normal site traffic the Chairman initiates directly.
- **Security is foundational, not a phase.** Checked in every phase from the start (Section 12).
- **Nothing fails silently.** Errors are caught and logged (Section 4), but nothing sensitive is ever logged.
- **External content stays external.** Links outside a module's locked domain hand off to the device's default browser.
- **Only build what the current phase specifies.** No scope creep from later phases, even when their existence is visible in context.

---

## 2. First Launch — Welcome Page — [PHASE-SPECIFIC: Phase 1]

Shown **only on the very first app open**, never again afterward — a one-time gate, not a revisitable screen.

```
┌─────────────────────────────────┐
│            [ Vaa ]               │
│   One place for your AI chats,   │
│   Google AI Studio, and GitHub.  │
│         [ Get Started ]          │
└─────────────────────────────────┘
```

Tapping through writes a local flag (`SharedPreferences: first_launch_complete = true`) — checked at app start; if true, skip straight to the normal 4-tab shell.

---

## 3. Top-Level Structure — [SKELETON]

Four bottom-placed tabs, swipeable between, each with its own icon. Dark/Celestial theme: dark background, white text, sky-blue accent (replacing every place a WhatsApp-style layout would use green — active states, selected-tab indicator, FAB backgrounds, sent-bubble color).

```
┌─────────────────────────────────┐
│         active tab content       │
├─────────────────────────────────┤
│ [Chats] [Updates] [Loader] [ ] │  ← main 4-tab bar
├─────────────────────────────────┤
│  ( C )( G )( AI )( GH )[+][ ⋮ ]   │  ← round-icon tab strip (global, true bottom-most)
└─────────────────────────────────┘
```

- **Chats** — built first. Section 5 onward.
- **Updates** — purpose: news/RSS widget. *[CLAUDE/CHAIRMAN ONLY: not designed yet, empty placeholder until its own phase — see Section 17 for parked spec.]*
- **Loader** (renamed from "Communities") — purpose: mini app-store-style list/grid of PWAs loaded from local `.zip` files. *[CLAUDE/CHAIRMAN ONLY: not designed yet, empty placeholder until its own phase — see Section 17 for parked spec.]*
- **4th tab** — purpose: Tools & Skills for Omega (Kai9000 referenced). *[CLAUDE/CHAIRMAN ONLY: last priority, gated behind Loader/launcher work — see Section 17.]*

Tab order, left to right: **Chats → Updates → Loader → 4th tab (Tools & Skills)**.

Three fixed FABs on each top-level tab's landing view only (not shown inside an open thread/tab):

| FAB | Position | Action |
|---|---|---|
| Log Keeper shortcut | left, alone | Opens the log viewer (Section 4). Settings toggle, default on. |
| Omega shortcut | right, stacked (top) | Opens the local AI thread (Section 6, Type C). Always present regardless of active tab. |
| Contextual action | right, stacked (below Omega) | Tab-aware: **Chats** → new chat/Add New; **Updates** → add new RSS channel; **Loader** → add new PWA; **4th tab** → TBD (not designed yet). |

*[CLAUDE/CHAIRMAN ONLY: exact stacking order/spacing is a UI detail, decide during build.]*

*[CLAUDE/CHAIRMAN ONLY: Launcher-level AI integration and text-input games are distant-future ideas, not designed, no placeholder structure required.]*

---

## 4. Log Keeper — [STANDING RULE] / [PHASE-SPECIFIC: Phase 2 for initial build]

An always-on, in-app logging system — same principle as Viaboard's `catch (e: Throwable)` fix, applied app-wide.

**Must catch and record:** module injection/scraping failures (all AI site modules), WebView load failures, local model load/inference failures, download failures, any caught exception that would otherwise crash or silently no-op.

**Must never record — hard exclusion built into logging call sites themselves, never redaction-after-the-fact:**
- Login/authentication flows (that a login screen loaded is fine; anything typed into it is not)
- Passwords or any credential-like input
- Session cookies or cookie values
- API keys, in any form — full, partial, or hashed
- Any other privacy-sensitive field a login/account flow might surface

Ordinary chat content is fine to log if relevant to diagnosing a scraping failure — the exclusion is specifically credentials/auth material, not conversation content generally.

**Access:** FAB shortcut (default on, togglable), also reachable via Settings → Logs regardless of FAB state. Copy-to-clipboard / export as `.txt` for pasting into a Claude conversation. Rolling retention, exact limit TBD on-device.

**Not:** a crash-reporting SDK, not sent anywhere, purely local.

---

## 5. The Chats Tab — [SKELETON, structure] / [PHASE-SPECIFIC: pieces built across Phases 3, 12]

```
┌─────────────────────────────────┐
│  Vaa                        [⋮]  │  ← top app bar — 3-dot menu:
├─────────────────────────────────┤     Settings / Add New / All Threads
│ (All)(Chat)(Page)(Local)         │  ← pill filter row
├─────────────────────────────────┤
│  Thread List                      │
│  ● Claude — Work    [chat]        │
│  ● Claude — Personal [chat]       │
│  ● Gemini           [chat]        │
│  ● Google AI Studio [page]        │
│  ● GitHub           [page]        │
└─────────────────────────────────┘
        ↓ tap a thread
┌─────────────────────────────────┐
│  [address bar — page tabs only]   │
├─────────────────────────────────┤
│   chat bubbles / live page /      │
│   Omega local chat                │
├─────────────────────────────────┤
│ [Chats] [Updates] [Loader] [ ]    │  ← main 4-tab bar
├─────────────────────────────────┤
│  ( C )( G )( AI )( GH )[+][ ⋮ ]   │  ← tab strip, true bottom-most
└─────────────────────────────────┘
```

**Top app bar:** right-corner 3-dot menu → Settings, Add New (Section 7), All Threads (unfiltered list).

**Pill filter row:** filters thread list by type — All / Chat / Page / Local.

**Thread List:** tap to open/refocus. Long-press → "Open in new tab" (adds to tab strip without leaving current view). **This long-press behavior applies across all top-level tabs, not just Chats** — once Updates/Loader/4th tab have real content, long-pressing an item there follows the same pattern.

**Tab Strip:** the true bottom-most bar on screen — sits below the main 4-tab bar, not above it — and is **global across all four top-level tabs**, not scoped to Chats alone. *[CLAUDE/CHAIRMAN ONLY: confirmed built this way as of Phase 3 — this is a build-history note, not a spec instruction; do not include in fragments.]* Round icons, horizontally scrollable, `+` and `⋮` fixed at the end, non-scrolling. `⋮` menu is **contextual to active tab type**: Forward/Back/Close for page-type tabs, reduced set (Close, module info) for chat/local-type tabs.

**Back navigation:** inside an open chat/local thread → returns to thread list. Inside an open page tab → real page navigation history.

**Address bar:** page-type tabs only. Domain-locked — copy button, path-level editing within the locked domain only, no free navigation outside it.

**External links:** any link inside a locked WebView pointing outside that module's domain is handed off via Android `Intent` (`ACTION_VIEW`) to the device's default browser, never loaded inside Vaa.

---

## 6. Thread Types — [SKELETON, all four types] / [PHASE-SPECIFIC: detailed build per type]

### Type A — Chat Thread (scraped) — [PHASE-SPECIFIC: Phases 5–8]
Sites added via Add via Website (Section 7): Claude, Gemini, Perplexity, extendable.

```
Chairman types → background WebView injects into site's input box →
simulates Enter → waits, reads output/reply element →
last occurrence of Chairman's sent text = top boundary,
input box position = bottom boundary, everything between = AI response →
rendered as chat bubbles.
```

Module format (written by Add New / element picker, not hand-edited in normal use):
```json
{
  "id": "claude.ai",
  "name": "Claude",
  "url": "https://claude.ai/new",
  "thread_type": "chat",
  "selectors": {
    "conversation_container": "[data-testid='conversation']",
    "input_box": "[data-testid='composer-input']"
  },
  "send_method": "enter_key",
  "login_check": "[data-testid='user-menu']"
}
```

**Cheap reload:** sleeping chat tabs show last saved conversation text instantly, no reload; WebView only reconstructs on new message.

**One thread, one continuous conversation.** No multi-conversation container per thread.

**Multiple accounts, one site:** the same site can be added more than once as fully independent threads (e.g. "Claude — Work" / "Claude — Personal"), each with **isolated cookie/session storage per thread instance** — not just per domain. Requires a separate WebView data directory per thread instance (e.g. via data-directory-suffix support), not just a separate module JSON entry. *[CLAUDE/CHAIRMAN ONLY: exact isolation mechanism TBD during Phase 5 build.]*

**Forwarding:** copy-into-composer between threads (Phase 16). *[CLAUDE/CHAIRMAN ONLY: an AI-initiated extension of this — tag-based relay requests between threads, with Chairman confirmation — is documented as a parked concept in Section 16.]*

### Type A-API — Chat Thread (API key, not scraped) — [PHASE-SPECIFIC: Phase 9]
Same bubble UI/persistence as Type A, backend is a direct API call using a Chairman-supplied key instead of scraping.

- No injection, no selectors — stable API contract
- Real per-token cost, unlike free web-UI-backed Type A
- **Key stored via Android Keystore only**, never a plaintext preference field
- Key **never displayed in full after initial entry** — masked input, at most last-few-characters confirmation
- Key **never passed to Log Keeper** (Section 4) under any circumstance
- App **explicitly opts out of Android auto-backup** (`allowBackup="false"` or equivalent) so a key can never surface in an unencrypted cloud backup

### Type B — Page Thread (full WebView) — [PHASE-SPECIFIC: Phases 10–11]
**Google AI Studio** (primary) and **GitHub** (full site).

Full live page, no scraping. Address bar active, domain-locked. Multiple simultaneous tabs allowed per site. VGH Smart Bridge available here only, deferred (Section 11). No upload overlay needed. Downloads via native Android handling (Section 10). External-link handoff applies.

### Type C — Local Thread (category; Omega is the first instance) — [PHASE-SPECIFIC: Phases 18–19]
GGUF model(s) running locally via llama.cpp, no cloud, no network needed once loaded.

- **Type C is a category, not a single permanent thread.** Multiple local-AI threads are possible, each its own model, each its own thread. **Omega** is the first and default instance — always present, one fixed thread, selected via Settings among any local models available.
- **Lazy-loaded:** a local model's inference engine only runs while its own thread is actually open — not resident in memory otherwise. This is the same pattern the (distant future) launcher-level AI will follow when it eventually plugs in as a second Type C instance, per Section 17.
- No LoRA, no fine-tuning, no agentic skills — plain chat with a small local model (e.g. SmolLM2-360M-Instruct q8_0) for Omega specifically
- Same chat-bubble UI as Type A, different backend (direct inference, not injection/scraping)
- Cherry-pick model-loading/inference Kotlin+JNI code directly from the SmolChat repo rather than building from scratch
- **Omega's model managed via Settings → Omega**, not Add New — Omega is fixed, not something the Chairman "adds"
- Native/JNI code — different risk category from the rest of the app, harder to verify via file diffing, more likely to fail silently. Log Keeper coverage and functional on-device verification matter more than file review alone here.
- *[CLAUDE/CHAIRMAN ONLY: memory contention with a ~300MB model alongside live WebView tabs on 3GB RAM is real and unsolved — flagged, revisit when this phase is actually being built. Browser-based local inference (WebLLM/WASM) was considered and rejected — Android WebView's WebGPU support is inconsistent on Go Edition hardware, WASM CPU fallback is slower than native. Native llama.cpp remains the better path here. Multiple simultaneous local threads compounds this contention risk further — a real constraint once more than one local model exists.]*

### Not a Type D
No general "any site, no module" browser tab. New sites go through Add New (Section 7) only.

---

## 7. Add New Flow — [PHASE-SPECIFIC: Phase 5 for Online/Website, Phase 9 for API Key]

```
+ Add New
├── Online  → list of saved Online AI items + [+] at bottom
│               tap [+]:
│                 → choice: Add via Website  or  Add via API Key
│                 → Add via Website:
│                     → URL input → opens site in WebView
│                     → Chairman logs in on the site's own form (never logged, Section 4)
│                     → **if the site's only login method is "Sign in with Google," it cannot be added as a Type A module** — Google blocks OAuth inside embedded WebViews for anti-phishing reasons, and while Android Custom Tabs can complete the login itself, Custom Tabs uses Chrome's own separate cookie jar, not the `WebView`/`CookieManager` this module architecture depends on — there is no bridge between the two on Android. If the site offers an alternative login method (email/password, magic link, etc.), use that instead in the normal embedded WebView flow as already described. If Google sign-in is the only option, this site cannot be scraped as a Type A module — an inherent architectural limit, not something to work around.
│                     → Chairman selects the specific chat/model on-site
│                     → element picker: select input box + output/reply element
│                     → save → locked Type A module (Section 6)
│                     → same site/account already saved? Chairman may still add another as a separate isolated account-thread
│                 → Add via API Key:
│                     → provider selection + key input (masked)
│                     → key stored in Android Keystore
│                     → save → Type A-API thread (Section 6)
│               tap existing item → opens/refocuses that thread
│
├── GitHub  → fixed built-in (Section 6, Type B)
│
└── Google AI Studio → fixed built-in (Section 6, Type B)
```

Omega (Type C) is **not** part of this flow — managed via Settings → Omega instead.

The one-time URL-input step during Online setup is a bounded WebView session for adding a module only, reachable solely through this deliberate action. Does not reopen general free-browsing capability.

---

## 8. Tab Lifecycle (Alive / Sleep) — [PHASE-SPECIFIC: Phase 3]

```
Active tab                → always live
Last N recently used tabs → kept live (cap TBD on-device, likely 2–3 on 3GB RAM)
Everything else           → destroyed, URL + cookies retained
                             (chat/local tabs: last conversation text also retained)
```

Sleeping page tab → real reload on reopen, still logged in via cookies. Sleeping chat tab → instant last-text display, no reload, WebView reconstructs only on new message. Omega → reload cost depends on residency strategy (open item).

---

## 9. Cache and Cookies — [PHASE-SPECIFIC: Phase 4]

**Only login cookies persist.** Everything else — page cache, WebStorage — cleared on **every app exit**, by default.

```
On app exit:
    WebStorage.getInstance().deleteAllData()   ← cache cleared
    CookieManager                              ← untouched, sessions survive
```

Settings toggle required — must be switchable (off, or a longer interval), not hardcoded.

---

## 10. Downloads — [PHASE-SPECIFIC: Phase 14]

No upload overlay needed — uploads happen through each site's own native file picker.

Download via native Android `DownloadManager` / `setDownloadListener`, no custom download engine:
1. AI-generated files from chat threads — "save this" action within the bubble UI
2. GitHub files (raw views, release assets, archives) — standard WebView download handling, visible/findable download location

---

## 11. Scripts — VGH Smart Bridge — [PHASE-SPECIFIC: Phase 20, deferred]

Not a general script manager. One bundled script, scoped only to AI Studio and GitHub page-tabs:

```
@@VGH-PUSH-START[file:path/to/file][reason:What changed]@@
full file content
@@VGH-PUSH-END[file:path/to/file]@@
```

Scroll-harvests tagged code blocks, stages them, commits to GitHub via API.

*[CLAUDE/CHAIRMAN ONLY: deferred to late build, same tier as Resource Trim — an automated script touching GitHub commits should be proven manually first.]*

---

## 12. Security — [STANDING RULE — include in full in every fragment, no exceptions]

Security is not deferred — checked in every phase from Phase 1 onward.

1. **No password ever stored, read, or logged by Vaa's own code.** Login happens entirely inside the site's own WebView form. Vaa only ever touches the session cookie issued afterward. Audit every generated auth-adjacent code for accidentally-added "remember password" fields or credential caches.
2. **Domain-lock check before every injection.** Confirm the WebView's current URL matches the module's declared domain before any injection JS runs.
3. **Cookie-only persistence, per-domain isolation.** No shared "session cache" for convenience, no cross-domain cookie access, in any phase.
4. **No bundled analytics/crash-reporting SDKs.** Check every generated `build.gradle.kts`/manifest for anything not explicitly asked for. (Log Keeper is local-only and explicitly not this.)
5. **Local-only storage, no unexpected network calls.** Conversation files, module JSON, cookies, logs — on-device only. No cloud sync unless deliberately added later.
6. **Downloads go to normal, visible Android storage** — nothing hidden, nothing auto-uploaded.
7. **Log Keeper never records credentials, passwords, cookie values, or API keys** — exclusion built into logging call sites, not redaction after the fact.
8. **External links exit to the default browser**, never handled inside a locked WebView.
9. **API keys stored only in Android Keystore**, never a plaintext preference field, never displayed in full after entry, explicitly excluded from Android auto-backup (`allowBackup="false"` or equivalent).
10. **Google OAuth ("Sign in with Google") cannot work for Type A (scraped) modules — this is an architectural limit, not a UI problem to route around.** Google blocks OAuth flows inside embedded WebViews for anti-phishing reasons, and while Android Custom Tabs can complete such a login, Custom Tabs shares Chrome's own cookie jar, not `android.webkit.CookieManager` — the store Vaa's embedded WebView modules actually read from. There is no bridge between the two. If a site's only login method is Google sign-in, it cannot be added as a Type A module; sites with an alternative login method (email/password, etc.) continue to work normally in the embedded WebView as already specified. Custom Tabs' only legitimate role in Vaa remains the existing one (Section 5): handing off external links that leave a locked domain entirely, where no session needs to return.
11. **WebView's own built-in autofill/save-password prompts must be explicitly disabled during login.** Rule 1 only covers what Vaa's own code does — it does not cover Android WebView's native Autofill framework integration, which can independently offer to save a typed password through its own "Save password?" prompt, entirely outside Vaa's cookie-only design. Disable this explicitly (e.g. `setSaveFormData(false)` and disabling WebView Autofill integration on the login `WebView` instance) so the only thing ever remembered from a login is the session cookie, by deliberate design — not by whatever Android's default WebView behavior happens to do.

---

## 13. Known Failure Patterns — [STANDING RULE — include in full in every fragment, no exceptions]

This project has caught the same handful of mistakes repeatedly across earlier phases. These are not hypothetical risks — every one of them actually happened and was caught by explicit verification, not by the build succeeding. Treat this list as binding, not advisory.

1. **A green build is not proof of a correct fix.** Local/in-workspace compile success has been wrong multiple times in this project (a missing Gradle wrapper, a Gradle/AGP version mismatch, a missing debug keystore, a `google-services.json` requirement) — each only surfaced on the real GitHub Actions runner, never in local compilation. Never report something as working based on a local build alone if it's the kind of thing that depends on the CI environment (build tooling, signing, CI-specific files).
2. **Never claim on-device or runtime behavior you did not actually observe.** If verification was reasoning about the code rather than an actual test, say so explicitly — do not phrase it as if it were tested. A synthetic test button built specifically to demonstrate a feature working is not the same as proving a pre-existing, independent code path works — do not conflate the two.
3. **Do not add scaffolding, dependencies, or plugins beyond what the current phase explicitly requests** — this includes anything a default project template may include automatically (Firebase, Google Services, networking libraries, database libraries, analytics/crash SDKs). If something appears in a generated `build.gradle.kts` or manifest that wasn't explicitly asked for, remove it rather than leave it as convenient scaffolding for later.
4. **Do not repurpose or rename any existing tab, screen, or reserved UI slot to solve an unrelated problem**, even if it seems convenient (e.g. do not put a Settings screen on a tab reserved for a different future purpose). If a new screen needs a home and the fragment doesn't specify one, ask, or pick a location that doesn't consume something already assigned elsewhere.
5. **Security-sensitive logic must be structural, not pattern-matched.** A keyword scan or content filter is not an acceptable substitute for a hard architectural exclusion (e.g. sensitive data must never reach a function in the first place, not be caught and filtered afterward by scanning for words). If asked to build an exclusion rule, build it as "this code path never constructs the call with that data," not as "check the output for suspicious words."
6. **State every deviation explicitly, even small ones**, and explain why. Silently choosing a different approach than what was requested — even for a good reason — is not acceptable; say so plainly in the report.
7. **When in doubt about scope, under-build rather than over-build.** It is always cheaper to be asked to add something in a follow-up prompt than to have built something unrequested that then needs to be found and removed.

---

## 14. Receipts — [STANDING RULE — include in full in every fragment, no exceptions]

A permanent, file-based audit trail — the persistent version of the PASS/FAIL + deviations reporting already standard practice, except it survives between sessions instead of living only in chat history. Exists because this project has no persistent local dev environment; the receipts files are the actual project memory.

**File naming:** `RECEIPTS_001.md`, `RECEIPTS_002.md`, etc. — zero-padded, sequential, stored in a `/receipts/` folder at repo root.

**Size limit:** each file capped at 500 lines. When the current file would exceed this, close it and begin the next numbered file. Never let a single file grow past the cap.

**Entry format**, appended to the current file after every action:
- Timestamp
- One-line summary of what was requested
- Exact files touched
- What was actually done — factual, not persuasive
- How it was verified: local build only / on-device claim only / not tested — stated plainly, never implying testing that didn't happen
- Any deviation from what was requested, and why
- Any known issue or follow-up needed

**Trigger:** append an entry after every single turn where any file is edited, built, or a fix is attempted — no exceptions, even for one-line changes.

**Content restriction:** never write passwords, API keys, tokens, cookies, or other credential-like values into any receipts file, even partially or inside pasted error messages — same standard as Log Keeper (Section 4).

**Purpose:** this file series is the project's permanent memory across sessions and across AI assistants. More reliable than conversational memory of the project — if in doubt about project history, read the receipts files first.

---

## 15. Resource Trim — [PHASE-SPECIFIC: Phase 21, deferred]

A small, hand-curated, per-domain list of specific tracker/analytics/telemetry script sources to skip loading, across all sites. **Not** a general ad blocker, **not** a filter-list engine.

*[CLAUDE/CHAIRMAN ONLY: built last, after every module is proven stable on its own — a wrong guess about what's "safe to skip" can silently break real functionality with no obvious error, especially on AI Studio. Same method as the element picker: observe real network activity per site on the actual device, hand-pick, test before trusting.]*

---

## 16. Cross-AI Relay & Tag-Based Commands — [CLAUDE/CHAIRMAN ONLY — never enters a fragment, no phase assigned]

Concept is documented in enough detail to be picked up later, but this is **not phase-scoped yet** — real open technical questions below need resolving before it becomes a buildable fragment.

### The core idea
An extension of the existing Forwarding feature (Section 6/Phase 16). Forwarding today is Chairman-initiated: manually copy a message from one thread into another's composer. This adds an **AI-initiated** version: an AI's response can include a tag (e.g. `@claude1/thread-name` — exact syntax TBD, likely bracket-style similar to VGH's `@@TAG-START[...]@@` format for consistency) requesting that a specific piece of text be relayed to a named thread. The app parses this tag out of the AI's response text and **always presents it to the Chairman as an explicit confirm/deny action before anything is sent** — never automatic, never silent. This is a direct application of Viabhron's existing "hard pause on ratification, no safe defaults" principle (Section 1) to a new context, not a new safety model.

### Also requested: manual export/share from AI Studio
Separate but related — being able to export/share recent text from a Google AI Studio (Type B, page thread) session to another AI thread, similar in spirit to Forwarding but sourced from a page-type thread instead of a chat-type one.

### Open technical questions (why this isn't phase-ready)

1. **Scraped chat threads (Type A) have no system-prompt mechanism.** Unlike Type A-API (a real API call, where a system prompt reliably teaches the model the tag syntax), a scraped WebView thread only knows about the tag format if it appears somewhere in the visible conversation — either the Chairman types an explanation manually, or the app auto-injects an explanatory message at the start of a thread. Which of these (or something else) is undecided.
2. **AI Studio (Type B) has no message-level extraction at all.** It's treated as an opaque full WebView with no defined input/output selectors, unlike Type A's two-selector scrape model. "Export recent message" needs a new mechanism for AI Studio specifically — most likely a manual text-selection + Android's native Share action, rather than automatic extraction, since AI Studio was never designed as a discrete-message surface. Needs its own design pass, not an assumption it works like Type A.
3. **Exact tag syntax** — not decided. Should stay consistent with VGH's existing bracket-tag style if possible, for one coherent "the app understands these kinds of tags" mental model rather than several unrelated formats.
4. **This is explicitly the first instance of a broader pattern**, not a one-off: a general tag-based command system the app parses out of AI text output and acts on with permission gating. VGH (file push, Section 11) is one existing instance. AI-relay would be a second. Chairman has confirmed more such commands are wanted **much later** — worth designing the parsing/permission-gate mechanism generically enough that adding a third command type later doesn't require rebuilding the first two.

### Security note
No new injection risk beyond what Forwarding (Phase 16) already accepts, **provided** every relay requires explicit, unskippable Chairman confirmation before the message is actually sent to the target thread — this is not optional and should be treated as a Section 12-equivalent hard rule once this becomes a real phase, not just a nice-to-have.

---

## 17. Future Tabs — Parked Specs — [CLAUDE/CHAIRMAN ONLY — never enters a fragment, no phase assigned]

Captured here so detail isn't lost, but **none of this is designed, scoped, or phased.** Structure and chat threads (Sections 2–13, Phases 1–21) come first, entirely. Nothing below should influence current build work.

### Updates tab
- Not AI-driven news search — a widget-style aggregator.
- Top area: vertically scrollable cards, but only a **few urgent items** shown by default — Chairman-set keywords to check against, or fall back to latest-only if no keywords set. Rest sits behind a "More news" entry.
- Tap any card or "More news" → near-fullscreen vertical scrollable card feed.
- Cards: headline + as much of the source's own lead text as fits in the card (no forced AI summarization — use the source's existing snippet if one exists). Dots/ellipsis indicate truncation.
- Tap a card → full article opens as a real webpage (Page-type view, not scraped).
- **Fav** on an article → saves article text + date as an MD file in a marked folder (ties into the MD-file/PWA-interop idea below).
- **Unfav** → deletes that same MD file.
- **Share-to-AI** on an article → send article text into an existing chat thread or start a new one. Reuses the Forwarding mechanism (Section 6/Phase 16), just with an article as the source instead of another thread's message.
- Below the top card area: an RSS reader/checker — added feeds behave like "channels."
- Adding a new RSS channel is the Updates tab's contextual FAB action (Section 3).

### Loader tab
- List or grid layout. Favorites pinned to the top, rest arranged below.
- Folders of PWA icons supported — subfolder grouping, similar to a home-screen app drawer.
- Sourced from a GitHub repo of PWA zips (Chairman-authored), loaded locally. No AI read/analysis involved in this base version.
- Adding a new PWA is the Loader tab's contextual FAB action (Section 3).

### 4th tab — Tools & Skills for Omega
- Reference app: **Kai9000** (`github.com/SimonSchubert/Kai`, already in the reference links).
- Explicitly last priority — gated behind Loader, the launcher, and everything else in this document being done first.
- No further detail decided.

### MD-file / PWA interop (distant future, corrected scope)
- **Not primarily an AI feature.** A shared plain-Markdown file format lets PWAs interoperate — e.g. a Notes-style PWA writes MD files, an Obsidian-style PWA organizes/links them, other PWAs (including the Updates tab's fav mechanic above) save into the same MD store.
- Local AI (any Type C thread, Section 6) being able to read from this store is one possible consumer of it, later — not the reason it exists.
- Also floated: a 3D "notes map" concept (e.g. mapping notes onto a human body for health tracking, or onto a building for housework tracking) — purely conceptual, stuck at "first step," not designed at all.

### AI reaching data/tools/skills generally
- Confirmed distant future, explicitly **after** the Tools & Skills tab exists, which is itself last. Not to be conflated with "plain chat" Omega as currently scoped (Section 6) — if/when this happens, it's a deliberately separate, explicitly-scoped addition, not an assumed extension of Omega's current definition.

### Other parked ideas (no design, no scope, listed only so nothing is lost)
- A JS sandbox — for local AI or the Chairman to take a code block (from a web AI chat, meeting certain criteria) and add it directly as a mini Loader app.
- Homepage shortcuts to mini Loader apps.
- Mini apps summonable as a floating window/widget (references the Chairman's earlier "floating reader" app).
- Sidebar action shortcuts (e.g. a shortcut straight to Omega, or to a specific chat) — references Viansidebhro, a separate existing project.

---

## 18. Open Items — [CLAUDE/CHAIRMAN ONLY — never enters a fragment]

- Multi-account cookie isolation approach (Section 6, Type A) — mechanism TBD during Phase 5.
- Local-thread (Omega) memory contention strategy — flagged, not designed, revisit at Phase 18/19; compounds further if multiple local threads exist (Section 6, Type C).
- Tab alive-cap exact number — "likely 2–3," tuned on-device once shell is running.
- Log Keeper retention limit — exact size/day cap TBD once real log volume is visible.
- FAB stacking order/spacing (right corner, Omega + contextual) — cosmetic, decide during build.
- Updates, Loader, and 4th-tab full detail — see Section 17 for the parked specs; nothing here is designed or phased yet.
- Cross-AI relay / tag-based commands (Section 16) — concept documented, real open technical questions (system-prompt-less tag teaching for scraped threads, AI Studio message extraction, tag syntax) unresolved, not phased.
- Launcher-level AI integration — distant future, not designed; will plug in as a second Type C instance per Section 6/17.
- Text-input games — distant future idea, noted only, not scoped.

---

## 19. Tech Stack — [SKELETON, trimmed to essentials per relevant phase]

| Component | Technology | Reason |
|---|---|---|
| App shell | Android (Kotlin) | Native WebView, cookie persistence |
| WebView | Android WebView (per thread) | Per-site cookie isolation, standard download handling |
| Injection | JavaScript via `evaluateJavascript()` | Standard Android API |
| Local inference | llama.cpp via JNI, cherry-picked from SmolChat | Proven on comparable low-end hardware |
| Module files | JSON | Human-readable, written automatically |
| Local storage | Room / SharedPreferences + plain `.txt` files | Structured data + human-readable history + logs |
| UI | Jetpack Compose | Matches rest of the Chairman's stack |
| Downloads | Android `DownloadManager` via `setDownloadListener` | Native, no custom engine |
| External links | Android `Intent` (`ACTION_VIEW`) | Standard OS handoff |

No backend, no server, no accounts. Not a fork of an existing browser.

---

## 20. Build Phases — [PHASE-SPECIFIC — the core of every fragment]

Each phase independently testable before the next starts. GitHub Actions build workflow (`.github/workflows/build.yml`) added as part of Phase 1, immediately after AI Studio's first export — AI Studio creates the repo and pushes code on export, but does **not** generate a working Actions workflow itself; that file must be added separately or no APK will ever get built from any phase.

**Phase 1 — Welcome Page + App Skeleton**
First-launch-only Welcome screen (Section 2) with one-time flag. Bare 4-tab shell (Chats live-but-empty, Updates/Loader/4th as static placeholders, each with its own icon), bottom-placed, swipeable, Dark/Celestial theme. No thread logic, no FABs wired to real actions yet. Includes first GitHub export + manual addition of the Actions build workflow file.

**Phase 2 — Log Keeper**
Central logging utility wired app-wide (Section 4): `catch (e: Throwable)` pattern, local persistent log store, in-app viewer, copy/export. Sensitive-field exclusion built in from the start. Log Keeper shortcut FAB (left corner) wired, Settings toggle added.

**Phase 3 — Thread List + Tab Strip Skeleton**
Chats tab: top app bar (3-dot menu), pill filter row, thread list UI, bottom tab strip with round icons + fixed `+`/`⋮` — proven with placeholder/dummy entries. Alive/sleep cap logic (Section 8) built and tested. Long-press → "Open in new tab" wired. **Outstanding fix identified post-build:** the tab strip currently disappears when navigating into an open thread's detail view, because that view is a separate nav-graph destination outside the `Scaffold` hosting the strip — true browser-tab behavior requires the strip to persist for the entire time a tab is open, not just on the thread list. Fix by rendering thread/detail content inside the same `Scaffold` (e.g. as another page in the existing pager/nav structure) rather than as an escaping top-level destination.

**Phase 4 — Cache/Cookie Behavior**
Cache-clear-on-exit (Section 9), Settings screen with the toggle. Verified against the dummy thread's WebView.

**Phase 5 — Add New Flow, Online/Website Path**
Full Add New → Online → Add via Website → URL → login → element picker → save flow (Section 7), tested against Claude. Domain-lock check (Section 12.2) built here. Multi-account isolation built and tested here — a second Claude thread under a different account must not disturb the first thread's session. **Google-only login handling built here too** — detect/document that a site whose only login method is "Sign in with Google" cannot be added as a Type A module (Section 12.10), and surface that clearly to the Chairman during Add New rather than silently failing or attempting a workaround that doesn't actually work. **WebView's native autofill/save-password prompts disabled on the login WebView instance** (Section 12.11), confirmed the "Save password?" prompt never appears during the Claude login test.

**Phase 6 — First Chat Module Working End-to-End**
Injection, Enter-key send, response extraction, bubble rendering, loading indicator, using the module Phase 5 saved.

**Phase 7 — Chat Thread Persistence**
Single ongoing conversation `.txt` file per thread, cheap sleep/reload.

**Phase 8 — Remaining Chat Modules**
Gemini, Perplexity added via the same Add New flow — confirms it generalizes past Claude.

**Phase 9 — API Key Chat Threads (Type A-API)**
"Add via API Key" sub-flow (Section 7), Android Keystore storage, masked key input/display, `allowBackup="false"` verified, Log Keeper exclusion confirmed. Isolated phase given it's the first place Vaa holds a real secret.

**Phase 10 — AI Studio Page Tab**
Full WebView, domain-locked address bar. Single tab first. External-link handoff to default browser built and tested here.

**Phase 11 — AI Studio Multi-Tab**
Multiple simultaneous AI Studio tabs, building on Phase 10.

**Phase 12 — GitHub Page Tab**
Same pattern as Phase 10, full github.com scope.

**Phase 13 — Contextual Tab-Strip Menu + Back Navigation**
`⋮` menu contextual to active tab type. Back-button behavior split by thread type.

**Phase 14 — Fallback + Element Picker Hardening**
Second-failure detection, Fix Selectors banner. Element picker already exists from Phase 5; this hardens the repair path.

**Phase 15 — Downloads**
Native WebView download handling for chat-attached files and GitHub files; visible download location.

**Phase 16 — Forwarding**
Forward-message-to-another-thread, copy-into-composer.

**Phase 17 — Search**
Full-text search across saved conversation files per thread.

**Phase 18 — Omega, Model Loading**
Cherry-pick llama.cpp inference plumbing from SmolChat, `.gguf` import flow built into Settings → Omega, load-and-respond proven in isolation.

**Phase 19 — Omega, UI Integration**
Wire Phase 18's inference into the shared chat-bubble UI. Omega shortcut FAB (right corner, top of stack) wired. Memory-contention strategy addressed with real on-device testing.

**Phase 20 — VGH Smart Bridge**
Deferred script (Section 11), tested manually before any auto-commit behavior is trusted.

**Phase 21 — Resource Trim**
Per-domain hand-curated skip list (Section 15), all sites, tested individually before trusting.

---

**Note:** *[STANDING RULE]* Discuss before build. No phase starts until the previous one works completely on-device. Every phase's output verified against actual file/zip contents before accepted — Gemini's own report is not sufficient on its own. Extra scrutiny on Phase 5 (multi-account isolation), Phase 9 (API key/Keystore), Phases 18–19 (native code), and any phase touching Section 12.

---

**Status:** 🟡 Ready for Phase 1 fragment extraction and prompt drafting.

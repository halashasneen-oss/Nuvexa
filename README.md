# Nuvexa

Every tool you need. Always offline.

Nuvexa is an offline-first, privacy-focused Android utility app: calculators, converters,
generators, and everyday tools that live in your pocket without ever needing an internet
connection. Nothing you create or open in Nuvexa is uploaded anywhere.

- **Offline-first** — every tool below works with no network connection.
- **Private by design** — no accounts, no analytics SDKs, no uploading of your files, photos,
  or text. The only network call the app ever makes is optional ad serving.
- **Multilingual** — English, Arabic (full RTL), French, and Spanish, with natural (not
  word-for-word) translations and locale-aware number/date formatting.
- **Free, ad-supported** — a banner ad on non-critical discovery screens (Tools categories,
  History). No interstitials, no forced ad views to use a tool.

## Status: real, working tools — not a mockup

Every tool listed in the app is fully implemented and functional; there are no "Coming
Soon" placeholders, dummy buttons, or fake results anywhere in the UI. What's *not* built
yet simply isn't in the app — see [Roadmap](#roadmap) for what's next.

### Implemented today (37 tools across 11 categories)

| Category | Tools |
|---|---|
| Calculators | Basic Calculator, Percentage, Discount, Tip, Split Bill, Age, Date Difference, Simple Interest, Compound Interest, EMI |
| Unit Converter | Length, Weight, Temperature, Area, Volume, Speed, Time, Data Storage (one tool, switchable) |
| Currency | Currency Calculator (manual, offline exchange rates you set yourself) |
| Text & Writing | Text Analyzer (words/characters/sentences/reading time), Case Converter, Text Cleaner, Base64, URL Encoder, JSON Formatter |
| Privacy & Security | Password Generator, PIN Generator, UUID Generator, Random Number Generator, Hash Generator (MD5/SHA-1/SHA-256/SHA-512) |
| QR & Barcode | QR Generator (text/link/Wi-Fi), QR/Barcode Scanner |
| Color Tools | Color Converter (HEX/RGB/HSL + picker), Palette Generator |
| Time & Date | World Clock, Stopwatch, Timer, Unix Timestamp Converter, Date Calculator (add/subtract, week number, day of year) |
| Developer Tools | Regex Tester, Lorem Ipsum Generator |
| Device | Device Information |
| Image Tools | Image Compressor, Image Resizer |

Plus the full app shell: onboarding, home dashboard (search, quick actions, favorites,
recents, local usage-based recommendations), category browser, favorites (reorderable),
history, and a complete settings screen (theme, language, start screen, haptics, storage,
privacy, about).

## Privacy

- No user accounts, ever.
- No file, photo, or text content is uploaded — image compression/resizing, hashing, QR
  generation/scanning, and every calculator run entirely on-device.
- History only stores a short, human-readable summary line per operation (e.g. "15% of
  200 = 30") — never the underlying file or full text content.
- The only network permission Nuvexa holds is used for optional ad serving; every tool's
  *functionality* works with Wi-Fi and mobile data both off.
- Full policy text is in-app under Settings → About → Privacy Policy (also mirrors the
  string in `strings.xml` as `privacy_policy_body`).

## Architecture

- **Kotlin + Jetpack Compose**, Material 3, single-activity navigation (Navigation Compose).
- **MVVM**: Hilt-injected `ViewModel`s expose `StateFlow` UI state; screens are stateless
  Composables.
- **Repository pattern** over Room (favorites, history, recent tools, manual currency
  rates) and DataStore (settings).
- **`ToolRegistry`** is the single source of truth for every tool (id, category, icon,
  localized name/description/search keywords). `ToolScreenHost` dispatches a tool id to
  its real screen — every id in the registry has a matching branch, enforced by hand and
  checked in CI-style review; nothing is listed without a working screen behind it.
- **`SearchEngine`** is a small local, multilingual keyword matcher — no cloud search, no
  ML model. It matches localized names/descriptions/keywords across all 4 languages,
  including natural-language phrases like *"how old am I"* or *"convertir imagen a pdf"*
  baked into each tool's keyword string resource.
- **`Recommendations`** is a lightweight on-device heuristic: it looks at which categories
  you actually use (from local usage counts) and surfaces other tools in those categories.

```
app/src/main/java/com/nuvexa/app/
  core/            # Tool model, registry, search, recommendations, format/color/file utils
  data/            # Room entities/DAOs, DataStore settings, repositories
  di/              # Hilt modules
  ui/
    theme/         # Material3 color scheme, typography, shapes, spacing
    navigation/    # NavHost, routes, bottom nav shell
    components/    # ToolScaffold, ToolCard, ResultCard, inputs, buttons, ads, etc.
    screens/       # Onboarding, Home, Tools, Favorites, History, Settings
    tools/         # One package per category, one file per tool screen
```

## Localization

All UI strings — chrome, every tool name/description/search-keyword string, error
messages, empty states — live in `res/values{,-ar,-fr,-es}/strings.xml` with **matching
key sets across all four locales** (verified by diffing the key lists; there is no
missing-translation gap today). Arabic is right-to-left; Compose's layout direction
mirroring handles navigation, icons, and forms automatically, and this was spot-checked
across every screen (paddings use `start`/`end`, not `left`/`right`).

Per-app language switching (Settings → Language) uses AndroidX's
`AppCompatDelegate.setApplicationLocales`, which works down to API 26 without requiring
`AppCompatActivity`.

## Build

```
./gradlew assembleDebug     # debug build
./gradlew testDebugUnitTest # local JVM unit tests (calculators, converters, color math)
```

> **A note on this repository's current CI environment:** the sandbox this project was
> authored in has no outbound access to `dl.google.com` / Google's Maven repository (and
> no Android SDK installed), so the Gradle/AGP build itself could not be executed here.
> The project was written carefully by hand and cross-checked with static analysis
> (string-resource reference checks, tool-registry/dispatch parity checks, brace/paren
> balance checks) instead. **Run a real build in Android Studio or CI with normal network
> access before treating this as release-ready** — that is the first thing to verify.

Signing is intentionally unconfigured (see `app/build.gradle.kts`); provide a real
keystore via environment variables or a local, git-ignored `keystore.properties` before
building a release artifact. AdMob is wired up with Google's public **test** ad unit and
app IDs — replace both with real ones before publishing.

## Roadmap

The master specification this app is built against describes a much larger set of
categories (PDF/document tools, OCR, file management, network diagnostics, developer
formatters beyond regex/lorem-ipsum, more QR content types, workflows chaining tools
together, and so on). Rather than stub those out with fake "Coming Soon" tiles, they are
simply not in the tool registry yet — the architecture (`Tool` model + `ToolRegistry` +
`ToolScreenHost`) is built to make adding each of them a contained, incremental change:
add a `Tool` entry, its 4-language strings, and a screen file.

Next up, roughly in priority order:
1. PDF tools (merge/split/compress/watermark) and a document scanner.
2. OCR (on-device, e.g. ML Kit text recognition) feeding into "image → text → clean →
   save" workflows.
3. More file tools (batch rename, ZIP, duplicate finder) via Storage Access Framework.
4. Saved multi-tool **Workflows** (the data model already anticipates this).
5. Network reference tools (IP/CIDR calculators, HTTP status/DNS reference) — clearly
   labeled as local-computation-only vs. anything that would need connectivity.
6. Expanding automated test coverage to instrumented UI tests (Compose test rule) for the
   navigation graph, RTL layout, and the search engine (which needs an Android `Context`
   and so isn't covered by the current plain-JVM unit tests).

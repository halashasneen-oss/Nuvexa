# Nuvexa

**Everyday tools. Private by design.**

Nuvexa is an offline-first Android utility app built for study, work, productivity and everyday tasks. It combines calculators, converters, text tools, privacy utilities, QR/PDF/OCR features and more in one multilingual app.

## Current release

- **Version:** 1.1.0 modernization branch
- **Platform:** Android 8.0+ (minSdk 26)
- **Target SDK:** 36
- **Tech:** Kotlin, Jetpack Compose, Material 3, Hilt, Room and DataStore
- **Languages:** English, Arabic (RTL), French and Spanish
- **Tool count:** 80 tools across 14 focused categories

## Product principles

- **Offline-first:** tool functionality is designed to work without an internet connection.
- **Private by design:** no user accounts and no uploading of user-entered text, documents or photos for tool processing.
- **Useful over numerous:** low-value reference-only utilities are deliberately removed instead of inflating the catalog.
- **Multilingual:** the full interface and searchable tool metadata support English, Arabic, French and Spanish.
- **Ad-supported:** Google AdMob is used for monetization without gating tool functionality.

## Advertising behavior

Nuvexa currently uses **both banner and interstitial AdMob ads**:

- Banner ads are shown on top-level/discovery areas and category browsing surfaces.
- Interstitial ads are preloaded and shown at a natural transition when leaving tools.
- Production limits interstitial frequency to **at most one ad every three eligible tool exits**.
- Debug builds use Google's public test ad IDs; release builds use the configured Nuvexa production AdMob IDs.
- Ads do not change how any tool calculates or processes user content.

This section reflects the actual current implementation. Older README text saying the app had no interstitial ads was outdated and has been removed.

## Tool catalog

| Category | Tools |
|---|---|
| Calculators | Basic, Percentage, Discount, Tip, Split Bill, Age, Date Difference, Simple Interest, Compound Interest, EMI, **Grade Goal**, Fraction, Ratio, Average, Markup, Profit Margin, Mortgage, Savings, Work Hours |
| Unit Converter | Length, Weight, Temperature, Area, Volume, Speed, Time and Data Storage in one switchable converter |
| Currency | Offline/manual Currency Calculator |
| Text & Writing | Text Analyzer, Case Converter, Text Cleaner, Base64, URL Encoder, JSON Formatter, **Text Compare**, Extract Numbers, Extract Emails, Extract URLs |
| Privacy & Security | Password Generator, **Password Strength**, PIN Generator, UUID Generator, Random Number Generator, Hash Generator, Passphrase Generator, HMAC Generator, AES-GCM Text Encryption |
| QR & Barcode | QR Generator and QR/Barcode Scanner |
| Color Tools | Color Converter and Palette Generator |
| Time & Date | World Clock, Stopwatch, Timer, Unix Timestamp Converter, Date Calculator, Working Days Calculator |
| Developer Tools | Regex Tester, Subnet/CIDR Calculator, XML Formatter, HTML Formatter, CSS Formatter, JWT Decoder |
| Math & Engineering | Prime Checker, Prime Generator, GCD & LCM, Factorial, Fibonacci Sequence, Binary/Hex/Octal Converter, Statistics Calculator |
| PDF & Documents | Images→PDF, Text→PDF, PDF→Images, PDF Viewer, Page Counter, Size Analyzer, Merge, Split, Rotate, Watermark, Organize Pages |
| OCR & Text Scanning | OCR from Image, OCR from Camera, OCR from PDF using the bundled on-device ML Kit text recognizer |
| Device | Device Information |
| Image Tools | Image Compressor and Image Resizer |

### Catalog cleanup in the modernization release

The following low-value utilities were removed from the visible product and their obsolete implementation files were cleaned up:

- Lorem Ipsum Generator
- HTTP Status Code Reference
- Port Reference

The Subnet/CIDR Calculator remains useful but now lives under **Developer Tools**, so a separate one-item Network category is no longer needed.

Three higher-value tools replace the removed items while keeping the catalog focused:

1. **Grade Goal Calculator** — calculates the average required on remaining coursework to hit a target final grade.
2. **Text Compare** — compares two texts locally and summarizes line, word and character differences.
3. **Password Strength** — checks password quality locally without storing or transmitting the entered password.

## Modernized interface

The modernization work introduces a more distinctive shared design system instead of styling individual screens inconsistently:

- Category-aware accent colors give calculators, security, PDF, text, OCR and other families their own visual identity.
- Tool icons now use consistent rounded badges and clearer hierarchy.
- Home includes a premium hero panel, redesigned quick actions and improved cards.
- Tool/category grids are adaptive instead of assuming exactly two columns on every device size.
- Shared inputs, buttons, result cards and tool headers have been refreshed so improvements apply throughout the app.
- Bottom navigation uses a floating rounded surface while preserving the existing banner-ad placement.
- Light and dark themes remain supported.

## Privacy

- No user accounts.
- Calculations, text processing, QR generation, image operations and document workflows are performed on-device.
- History stores short human-readable operation summaries rather than original files or full source content.
- Internet/network permissions are used for optional ad delivery; core utility functionality remains local.
- Privacy policy: `docs/privacy-policy.html` and Settings → About → Privacy Policy.

## PDF implementation note

Android's built-in APIs expose `PdfRenderer` for rasterizing existing pages and `PdfDocument` for creating new PDFs. Therefore Merge, Split, Rotate, Watermark and Organize rebuild existing pages from rendered images. Those operations are functional, but rebuilt pages do not preserve original selectable/vector text. Images→PDF and Text→PDF create new PDFs directly. Password-protected PDFs are not decrypted.

## Architecture

- **Kotlin + Jetpack Compose**, Material 3 and Navigation Compose.
- **MVVM** with Hilt-injected ViewModels and `StateFlow` UI state.
- **Room** for favorites/history/recent tools/manual currency rates.
- **DataStore** for settings.
- **`ToolRegistry`** is the single source of truth for visible tools.
- **`ToolScreenHost`** maps every registry ID to a real screen.
- **`SearchEngine`** performs local multilingual keyword matching.
- **Recommendations** use local usage history/category affinity.

```text
app/src/main/java/com/nuvexa/app/
  core/            models, registry, search, recommendations and utilities
  data/            Room, DataStore and repositories
  di/              Hilt modules
  ui/
    theme/         colors, typography, shapes and spacing
    navigation/    NavHost and app shell
    components/    shared cards, inputs, buttons, result UI and ads
    screens/       Home, Tools, Favorites, History, Settings, Onboarding
    tools/         functional tool screens grouped by category
```

## Build and verification

```bash
./gradlew test --stacktrace
./gradlew lint --stacktrace
./gradlew assembleDebug --stacktrace
```

GitHub Actions (`.github/workflows/android-build.yml`) runs unit tests, lint and a debug APK build on pushes and pull requests. If release signing secrets are configured, CI also creates signed release APK/AAB artifacts.

Release signing is loaded only from environment variables/secrets; no keystore is committed to the repository.

## AdMob

AdMob configuration is intentionally separated by build type:

- Debug uses Google's official test app/unit IDs.
- Release uses the Nuvexa production app, banner and interstitial IDs already configured in `app/build.gradle.kts`.
- `AdsInitializer` serializes SDK initialization.
- `InterstitialAdManager` preloads a single interstitial and controls display frequency.

The modernization changes do **not** alter the production ad IDs, banner placement logic or interstitial frequency policy.

## Roadmap

Priority is quality rather than raw tool count. Candidate future improvements include:

1. Document scanning with reliable edge detection and a carefully evaluated offline/privacy story.
2. More capable non-rasterized PDF editing through a vetted PDF library.
3. File utilities such as ZIP, batch rename and duplicate detection through Android's Storage Access Framework.
4. Saved multi-tool workflows such as image → OCR → clean text → export.
5. More structured QR types such as contact, email, SMS, location and calendar entries.
6. Broader Compose UI/instrumentation tests for navigation, RTL behavior and critical tool workflows.

# Nuvexa

**Powerful tools. Private by design.**

Nuvexa is an offline-first Android utility app built for everyday calculations, files,
text, privacy, study and developer work. Core tool functionality runs on-device; network
access is used for optional Google AdMob advertising.

## Current status

- **80 curated tools across 15 categories** — real screens, no "Coming Soon" tiles.
- **Kotlin + Jetpack Compose + Material 3** with a shared modern design system.
- **English, Arabic (RTL), French and Spanish**.
- **Offline-first** calculators, converters, PDF utilities, OCR, QR, image and security tools.
- **Local history/favorites/recents** using Room and DataStore.
- `app/build.gradle.kts` currently reports **version 1.1.0** (`versionCode = 6`).

## Tool catalog

| Category | Tools |
|---|---|
| Calculators | Basic, Scientific, Percentage, Discount, Tip, Split Bill, Age, Date Difference, Simple Interest, Compound Interest, EMI, Fraction, Ratio, Average, Markup, Profit Margin, Mortgage, Savings, Work Hours, Fuel & Trip Cost |
| Unit Converter | Length, Weight, Temperature, Area, Volume, Speed, Time and Data Storage in one switchable converter |
| Currency | Offline/manual Currency Calculator |
| Text & Writing | Text Analyzer, Case Converter, Text Cleaner, Base64, URL Encoder, JSON Formatter, Extract Numbers, Extract Emails, Extract URLs |
| Privacy & Security | Password Generator, Password Strength Audit, File Checksum (SHA-256/SHA-512), PIN Generator, UUID Generator, Random Number Generator, Hash Generator, Passphrase Generator, HMAC Generator, AES-GCM Text Encryption |
| QR & Barcode | QR Generator, QR/Barcode Scanner |
| Color Tools | Color Converter, Palette Generator |
| Time & Date | World Clock, Stopwatch, Timer, Unix Timestamp Converter, Date Calculator, Working Days Calculator |
| Developer Tools | Regex Tester, XML Formatter, HTML Formatter, CSS Formatter, JWT Decoder |
| Device | Device Information |
| Image Tools | Image Compressor, Image Resizer |
| Math & Engineering | Prime Checker, Prime Generator, GCD & LCM, Factorial, Fibonacci, Binary/Hex/Octal Converter, Statistics Calculator |
| Network Tools | Subnet/CIDR Calculator |
| PDF & Documents | Images→PDF, Text→PDF, PDF→Images, PDF Viewer, PDF Inspector, Merge, Split, Rotate, Watermark, Organize Pages |
| OCR & Text Scanning | OCR from Image, OCR from Camera, OCR from PDF |

### Why the catalog changed

Nuvexa deliberately favors tools that perform a useful action over static reference-list
screens. The HTTP status reference, port reference and Lorem Ipsum generator were removed
from the visible catalog. Separate PDF Page Counter and PDF Size Analyzer screens were
replaced by one **PDF Inspector** that provides page count, total size and average page size
together.

The refresh adds five higher-value tools without bloating the catalog:

1. **Scientific Calculator** — expression parser with precedence, powers, roots, logarithms,
   trigonometry and degree/radian modes.
2. **Fuel & Trip Cost** — estimates fuel required, total cost and cost per passenger.
3. **Password Strength Audit** — local strength/entropy checks; entered passwords are not
   saved to history.
4. **File Checksum** — computes SHA-256 and SHA-512 in one pass without uploading the file.
5. **PDF Inspector** — combines the most useful PDF information in a single screen.

## Design system

The UI uses a shared Compose design layer so every tool inherits the same visual language:

- Indigo/violet/blue brand palette with light and dark schemes.
- Rounded Material icons with branded gradient icon badges.
- Larger rounded surfaces, clearer hierarchy and consistent card elevation.
- Adaptive tool/category grids for different phone widths.
- Modernized home hero, search, quick actions, category cards and bottom navigation.
- A shared tool header that shows the tool icon, description and local-first privacy cue.
- Localized numeric input normalization for `.` / `,` / Arabic decimal separators and
  Arabic/Persian digits before existing calculators parse values.

## Advertising — current implementation

Nuvexa is free and ad-supported. The app currently contains **both banner and interstitial
AdMob ads**.

- **Banner ads** are shown on top-level screens and on category browsing screens.
- **Interstitial ads** are offered only at a natural transition: when leaving a tool.
- In a release build, the interstitial manager shows at most one ad every **three tool exits**.
- Debug builds use Google's public test ad units and make every eligible exit testable.
- Ad loading is initialized through the shared `AdsInitializer` and the interstitial is
  preloaded so tool navigation is not dependent on ad load timing.
- If an interstitial is unavailable or fails to show, navigation continues normally.

Ad configuration is defined in `app/build.gradle.kts` and ad behavior lives in
`BannerAd.kt`, `AdsInitializer.kt` and `InterstitialAdManager.kt`. The modernization refresh
does **not** change the existing ad IDs, frequency policy or ad manager behavior.

## Privacy

- No Nuvexa account is required.
- Core tools do not upload user text, photos or documents.
- OCR uses the bundled on-device Google ML Kit text-recognition model.
- Password Strength Audit never writes the tested password to history.
- File Checksum reads the selected file locally and calculates both digests on-device.
- Favorites, recent tools, settings and short history summaries are stored locally.
- Advertising is the app's intended network activity; AdMob may process advertising-related
  data under Google's own policies.

The published policy is in `docs/privacy-policy.html`, and the in-app policy is available
under **Settings → About → Privacy Policy**.

## PDF implementation note

Android's built-in PDF APIs do not provide a full structural editor. Existing PDF editing
operations such as Merge, Split, Rotate, Watermark and Organize use a rasterized workflow:
pages are rendered to high-resolution images and rebuilt into a new PDF. That makes the
operations functional without a third-party PDF engine, but rebuilt page text is not the
original selectable/vector text. Password-protected PDFs are reported as unsupported rather
than failing silently.

Images→PDF and Text→PDF create new PDFs directly and are not subject to that same existing-PDF
structure limitation.

## Architecture

```text
app/src/main/java/com/nuvexa/app/
  core/
    model/          Tool and category models
    registry/       ToolRegistry — visible catalog source of truth
    search/         Local multilingual search and recommendations
    util/           Calculators, formatters, PDF/OCR/crypto helpers, ad coordination
  data/
    local/          Room + DataStore
    repository/     Favorites, history, recents, settings, currency data
  di/               Hilt modules
  ui/
    components/     Shared inputs, buttons, cards, result surfaces, ads, icon badges
    navigation/     Root shell, routes and NavHost
    screens/        Home, Tools, Favorites, History, Settings, Onboarding
    theme/          Color, typography, shapes and spacing
    tools/          Functional tool screens grouped by category
```

`ToolRegistry` and `ToolScreenHost` are kept in parity: a tool should never be shown unless
there is a real screen behind its id.

## Build and verification

```bash
./gradlew test
./gradlew lint
./gradlew assembleDebug
```

GitHub Actions runs unit tests, Android lint and a debug APK build on pushes and pull
requests. Release APK/AAB builds run when release-signing secrets are configured.

The scientific-expression parser has dedicated JVM tests covering precedence,
right-associative powers, unary minus, scientific functions, degree mode, localized decimal
characters and invalid math.

## Release signing

No keystore is committed to the repository. Release signing is activated only when the
expected environment variables are available (`KEYSTORE_FILE`, passwords and alias). GitHub
Actions can decode the keystore from configured repository secrets before building a signed
release artifact.

## Next high-value directions

Future additions should stay selective rather than increasing the tool count for its own
sake. Strong candidates include a camera document scanner, real structural PDF editing via a
purpose-built PDF library, privacy-focused image metadata removal, and saved multi-tool
workflows such as **photo → OCR → clean text → save**.

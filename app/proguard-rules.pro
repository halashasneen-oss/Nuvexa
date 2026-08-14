# Nuvexa release shrinking/obfuscation rules.
# AndroidX, Room, Hilt and Compose ship their own consumer ProGuard rules,
# so this file only covers project-specific and third-party edge cases.

# Keep Room entities/DAOs generated code working with reflection-free access.
-keep class com.nuvexa.app.data.local.entity.** { *; }

# ZXing embedded barcode scanning.
-keep class com.google.zxing.** { *; }
-keep class com.journeyapps.barcodescanner.** { *; }

# Strip verbose/debug logging in release builds; keep warnings and errors.
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
}

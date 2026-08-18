gemini "Using the attached ClearStreak_Spec_v2.md document as your primary technical reference, generate a local-first Android Kotlin project for ClearStreak with the following constraints:
1. AndroidManifest.xml: exclude android.permission.INTERNET from default flavor. Add a 'release' flavor with minimal networking for IAP.
2. Dependencies: SQLCipher (net.zetetic:android-database-sqlcipher), BiometricPrompt (androidx.biometric:biometric).
3. MainActivity: BiometricPrompt gate before any database access. Use CryptoObject for key derivation.
4. UI: 4-tier Urge Grid (Clear, Passing, White-Knuckling, Critical) with HALT trigger row.
5. Crisis Intercept: Fullscreen Red Rescue Hub with tel:// dialers, map intents, 60-sec grounding timer, and 4-7-8 breathing circle animation.
6. Database: SQLCipher with StrongBox-backed key. Exclude from backup.
7. Widget: Jetpack Glance widget reading streak data from shared sandbox storage." --files ClearStreak_Spec_v2.md
# SQLCipher rules
-keep class net.zetetic.database.sqlcipher.** { *; }
-keep class net.zetetic.database.** { *; }
-dontwarn net.zetetic.database.sqlcipher.**

# Biometric & Glance rules
-keep class androidx.biometric.** { *; }
-keep class androidx.glance.** { *; }

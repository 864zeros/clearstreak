# SQLCipher rules (net.zetetic:android-database-sqlcipher uses the net.sqlcipher package)
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }
-dontwarn net.sqlcipher.**

# Biometric & Glance rules
-keep class androidx.biometric.** { *; }
-keep class androidx.glance.** { *; }

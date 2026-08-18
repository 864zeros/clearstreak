package com.eight64zeros.clearstreak

import android.app.Application
import net.zetetic.database.sqlcipher.SQLiteDatabase

class ClearStreakApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize SQLCipher native binaries
        SQLiteDatabase.loadLibs(this)
    }
}

package com.eight64zeros.clearstreak

import android.app.Application
import net.sqlcipher.database.SQLiteDatabase

class ClearStreakApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize SQLCipher native binaries
        SQLiteDatabase.loadLibs(this)
    }
}

package com.eight64zeros.clearstreak.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase as AndroidSQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper as AndroidSQLiteOpenHelper
import com.eight64zeros.clearstreak.model.CheckIn
import com.eight64zeros.clearstreak.model.HaltTrigger
import com.eight64zeros.clearstreak.model.Journey
import com.eight64zeros.clearstreak.model.JourneyCategory
import com.eight64zeros.clearstreak.model.UrgeLevel
import net.sqlcipher.database.SQLiteDatabase as SQLCipherDatabase
import net.sqlcipher.database.SQLiteOpenHelper as SQLCipherOpenHelper
import java.io.File

class DatabaseManager(private val context: Context) {

    private val coreDbHelper = CoreDbHelper(context)
    private var encDbHelper: EncryptedDbHelper? = null
    private var encryptedDb: SQLCipherDatabase? = null

    // ==========================================
    // Biometric / Hardware Unlock Gate
    // ==========================================

    @Synchronized
    fun unlockEncryptedDatabase(passphrase: ByteArray) {
        val current = encryptedDb
        if (current != null && current.isOpen) return
        val helper = EncryptedDbHelper(context)
        this.encDbHelper = helper
        this.encryptedDb = helper.getWritableDatabase(passphrase)
    }

    @Synchronized
    fun lockEncryptedDatabase() {
        encryptedDb?.close()
        encryptedDb = null
        encDbHelper?.close()
        encDbHelper = null
    }

    val isEncryptedDbUnlocked: Boolean
        get() = encryptedDb?.isOpen == true

    // ==========================================
    // Journeys (streak_core.db)
    // ==========================================

    fun getJourneys(includeArchived: Boolean = false): List<Journey> {
        val db = coreDbHelper.readableDatabase
        val selection = if (includeArchived) null else "is_archived = 0"
        val cursor = db.query(
            "journeys",
            null,
            selection,
            null,
            null,
            null,
            "created_at ASC"
        )
        val journeys = mutableListOf<Journey>()
        cursor.use {
            while (it.moveToNext()) {
                journeys.add(
                    Journey(
                        id = it.getString(it.getColumnIndexOrThrow("id")),
                        title = it.getString(it.getColumnIndexOrThrow("title")),
                        category = JourneyCategory.fromString(it.getString(it.getColumnIndexOrThrow("category"))),
                        customLabel = it.getColumnIndexOrThrow("custom_label").let { i -> if (it.isNull(i)) null else it.getString(i) },
                        startTimestamp = it.getLong(it.getColumnIndexOrThrow("start_timestamp")),
                        dailyCostSavings = it.getDouble(it.getColumnIndexOrThrow("daily_cost_savings")),
                        isArchived = it.getInt(it.getColumnIndexOrThrow("is_archived")) == 1,
                        suppressGameTools = it.getInt(it.getColumnIndexOrThrow("suppress_game_tools")) == 1,
                        createdAt = it.getLong(it.getColumnIndexOrThrow("created_at"))
                    )
                )
            }
        }
        return journeys
    }

    fun insertJourney(journey: Journey) {
        val db = coreDbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id", journey.id)
            put("title", journey.title)
            put("category", journey.category.dbValue)
            put("custom_label", journey.customLabel)
            put("start_timestamp", journey.startTimestamp)
            put("daily_cost_savings", journey.dailyCostSavings)
            put("is_archived", if (journey.isArchived) 1 else 0)
            put("suppress_game_tools", if (journey.suppressGameTools) 1 else 0)
            put("created_at", journey.createdAt)
        }
        db.insertWithOnConflict("journeys", null, values, AndroidSQLiteDatabase.CONFLICT_REPLACE)
    }

    fun updateJourney(journey: Journey) {
        val db = coreDbHelper.writableDatabase
        val values = ContentValues().apply {
            put("title", journey.title)
            put("category", journey.category.dbValue)
            put("custom_label", journey.customLabel)
            put("start_timestamp", journey.startTimestamp)
            put("daily_cost_savings", journey.dailyCostSavings)
            put("is_archived", if (journey.isArchived) 1 else 0)
            put("suppress_game_tools", if (journey.suppressGameTools) 1 else 0)
        }
        db.update("journeys", values, "id = ?", arrayOf(journey.id))
    }

    fun archiveJourney(journeyId: String) {
        val db = coreDbHelper.writableDatabase
        val values = ContentValues().apply {
            put("is_archived", 1)
        }
        db.update("journeys", values, "id = ?", arrayOf(journeyId))
    }

    // ==========================================
    // Check-Ins & Journal Notes (recovery_enc.db)
    // ==========================================

    fun insertCheckIn(checkIn: CheckIn) {
        val db = checkEncryptedDb()
        val values = ContentValues().apply {
            put("id", checkIn.id)
            put("journey_id", checkIn.journeyId)
            put("timestamp", checkIn.timestamp)
            put("mood_score", checkIn.moodScore)
            put("urge_level", checkIn.urgeLevel.name)
            put("halt_trigger", checkIn.haltTrigger?.name)
            put("note_encrypted", checkIn.noteEncrypted?.toByteArray(Charsets.UTF_8))
            put("is_slip", if (checkIn.isSlip) 1 else 0)
            put("is_crisis_intercept", if (checkIn.isCrisisIntercept) 1 else 0)
        }
        db.insertWithOnConflict("check_ins", null, values, SQLCipherDatabase.CONFLICT_REPLACE)
    }

    fun getCheckInsForJourney(journeyId: String): List<CheckIn> {
        val db = checkEncryptedDb()
        val cursor = db.query(
            "check_ins",
            null,
            "journey_id = ?",
            arrayOf(journeyId),
            null,
            null,
            "timestamp ASC"
        )
        return parseCheckInsCursor(cursor)
    }

    fun getAllCheckIns(): List<CheckIn> {
        val db = checkEncryptedDb()
        val cursor = db.query(
            "check_ins",
            null,
            null,
            null,
            null,
            null,
            "timestamp DESC"
        )
        return parseCheckInsCursor(cursor)
    }

    fun getJournalEntries(): List<CheckIn> {
        val db = checkEncryptedDb()
        val cursor = db.query(
            "check_ins",
            null,
            "note_encrypted IS NOT NULL",
            null,
            null,
            null,
            "timestamp DESC"
        )
        return parseCheckInsCursor(cursor)
    }

    private fun parseCheckInsCursor(cursor: Cursor): List<CheckIn> {
        val list = mutableListOf<CheckIn>()
        cursor.use {
            while (it.moveToNext()) {
                val noteBlob = it.getBlob(it.getColumnIndexOrThrow("note_encrypted"))
                val noteText = noteBlob?.let { b -> String(b, Charsets.UTF_8) }

                list.add(
                    CheckIn(
                        id = it.getString(it.getColumnIndexOrThrow("id")),
                        journeyId = it.getString(it.getColumnIndexOrThrow("journey_id")),
                        timestamp = it.getLong(it.getColumnIndexOrThrow("timestamp")),
                        moodScore = if (it.isNull(it.getColumnIndexOrThrow("mood_score"))) null else it.getInt(it.getColumnIndexOrThrow("mood_score")),
                        urgeLevel = UrgeLevel.fromString(it.getString(it.getColumnIndexOrThrow("urge_level"))),
                        haltTrigger = if (it.isNull(it.getColumnIndexOrThrow("halt_trigger"))) null else HaltTrigger.fromString(it.getString(it.getColumnIndexOrThrow("halt_trigger"))),
                        noteEncrypted = noteText,
                        isSlip = it.getInt(it.getColumnIndexOrThrow("is_slip")) == 1,
                        isCrisisIntercept = it.getInt(it.getColumnIndexOrThrow("is_crisis_intercept")) == 1
                    )
                )
            }
        }
        return list
    }

    private fun checkEncryptedDb(): SQLCipherDatabase {
        return encryptedDb ?: throw IllegalStateException("Recovery database is locked. Biometric authentication required.")
    }

    // ==========================================
    // Internal SQLite Open Helpers
    // ==========================================

    private class CoreDbHelper(context: Context) : AndroidSQLiteOpenHelper(context, CORE_DB_NAME, null, DB_VERSION) {
        override fun onCreate(db: AndroidSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE journeys (
                    id TEXT PRIMARY KEY,
                    title TEXT NOT NULL,
                    category TEXT NOT NULL,
                    custom_label TEXT,
                    start_timestamp INTEGER NOT NULL,
                    daily_cost_savings REAL DEFAULT 0.0,
                    is_archived INTEGER DEFAULT 0,
                    suppress_game_tools INTEGER DEFAULT 0,
                    created_at INTEGER DEFAULT (strftime('%s','now'))
                );
                """.trimIndent()
            )
        }

        override fun onUpgrade(db: AndroidSQLiteDatabase, oldVersion: Int, newVersion: Int) {
            if (oldVersion < 3) {
                db.execSQL("ALTER TABLE journeys ADD COLUMN suppress_game_tools INTEGER DEFAULT 0")
            }
            if (oldVersion < 4) {
                // Coping cards removed (blueprint §2): drop the obsolete table
                db.execSQL("DROP TABLE IF EXISTS coping_cards")
            }
            if (oldVersion < 5) {
                // New category taxonomy + custom_label; recreate to drop the old category CHECK.
                db.execSQL(
                    """
                    CREATE TABLE journeys_new (
                        id TEXT PRIMARY KEY,
                        title TEXT NOT NULL,
                        category TEXT NOT NULL,
                        custom_label TEXT,
                        start_timestamp INTEGER NOT NULL,
                        daily_cost_savings REAL DEFAULT 0.0,
                        is_archived INTEGER DEFAULT 0,
                        suppress_game_tools INTEGER DEFAULT 0,
                        created_at INTEGER DEFAULT (strftime('%s','now'))
                    );
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO journeys_new (id, title, category, custom_label, start_timestamp, daily_cost_savings, is_archived, suppress_game_tools, created_at)
                    SELECT id, title,
                        CASE category WHEN 'substance' THEN 'alcohol' WHEN 'smoking' THEN 'vape' ELSE category END,
                        NULL, start_timestamp, daily_cost_savings, is_archived, suppress_game_tools, created_at
                    FROM journeys;
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE journeys")
                db.execSQL("ALTER TABLE journeys_new RENAME TO journeys")
            }
        }
    }

    private class EncryptedDbHelper(context: Context) : SQLCipherOpenHelper(context, ENC_DB_NAME, null, DB_VERSION) {
        override fun onCreate(db: SQLCipherDatabase) {
            db.execSQL(
                """
                CREATE TABLE check_ins (
                    id TEXT PRIMARY KEY,
                    journey_id TEXT NOT NULL,
                    timestamp INTEGER NOT NULL,
                    mood_score INTEGER,
                    urge_level TEXT NOT NULL CHECK(urge_level IN ('CLEAR','PASSING','WHITE_KNUCKLING','CRITICAL')),
                    halt_trigger TEXT CHECK(halt_trigger IN ('HUNGRY','ANGRY','LONELY','TIRED','STRESSED','HOPELESS','GENERAL')),
                    note_encrypted BLOB,
                    is_slip INTEGER DEFAULT 0,
                    is_crisis_intercept INTEGER DEFAULT 0,
                    coping_card_id TEXT
                );
                """.trimIndent()
            )
        }

        override fun onUpgrade(db: SQLCipherDatabase, oldVersion: Int, newVersion: Int) {
            if (oldVersion < 2) {
                // Drop and recreate or migrate check_ins table
                db.execSQL("DROP TABLE IF EXISTS check_ins")
                onCreate(db)
            }
        }
    }

    companion object {
        private const val CORE_DB_NAME = "streak_core.db"
        private const val ENC_DB_NAME = "recovery_enc.db"
        private const val DB_VERSION = 5
    }
}

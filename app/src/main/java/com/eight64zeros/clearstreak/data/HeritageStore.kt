package com.eight64zeros.clearstreak.data

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.eight64zeros.clearstreak.model.Verse

/**
 * Offline Heritage Vault store (blueprint §3). A plaintext, non-sensitive SQLite
 * database with an FTS5 full-text index over public-domain content. Seeded from
 * the bundled asset `proverbs_web.txt` (World English Bible, public domain) on
 * first run.
 *
 * FTS5 is available in Android's system SQLite from API 26 (this app's minSdk).
 * The 1939 AA Big Book is deliberately NOT included — copyright is contested and
 * counsel-gated (see PROGRESS.md §5).
 */
class HeritageStore(context: Context) {

    private val helper = HeritageDbHelper(context.applicationContext)

    fun availableChapters(): Set<Int> {
        val db = helper.readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT chapter FROM proverbs", null)
        val set = mutableSetOf<Int>()
        cursor.use { while (it.moveToNext()) it.getString(0).toIntOrNull()?.let(set::add) }
        return set
    }

    fun proverbsForChapter(chapter: Int): List<Verse> {
        val db = helper.readableDatabase
        return db.rawQuery(
            "SELECT chapter, verse, text FROM proverbs WHERE chapter = ? ORDER BY CAST(verse AS INTEGER)",
            arrayOf(chapter.toString())
        ).toVerses()
    }

    fun search(raw: String): List<Verse> {
        val q = raw.trim()
        if (q.isEmpty()) return emptyList()
        // Build a safe FTS5 prefix query from the user's words.
        val match = q.split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .joinToString(" ") { "\"" + it.replace("\"", "") + "\"*" }
        return try {
            helper.readableDatabase.rawQuery(
                "SELECT chapter, verse, text FROM proverbs WHERE proverbs MATCH ? ORDER BY rank LIMIT 50",
                arrayOf(match)
            ).toVerses()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun Cursor.toVerses(): List<Verse> {
        val list = mutableListOf<Verse>()
        use {
            while (it.moveToNext()) {
                list.add(
                    Verse(
                        chapter = it.getString(0).toIntOrNull() ?: 0,
                        verse = it.getString(1).toIntOrNull() ?: 0,
                        text = it.getString(2)
                    )
                )
            }
        }
        return list
    }

    companion object {
        const val SERENITY_PRAYER =
            "God, grant me the serenity to accept the things I cannot change, " +
                "the courage to change the things I can, and the wisdom to know the difference."
    }

    private class HeritageDbHelper(private val context: Context) :
        SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("CREATE VIRTUAL TABLE proverbs USING fts5(chapter UNINDEXED, verse UNINDEXED, text)")
            seedFromAsset(db)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS proverbs")
            onCreate(db)
        }

        private fun seedFromAsset(db: SQLiteDatabase) {
            try {
                context.assets.open(ASSET_NAME).bufferedReader().useLines { lines ->
                    db.beginTransaction()
                    try {
                        val stmt = db.compileStatement(
                            "INSERT INTO proverbs(chapter, verse, text) VALUES (?, ?, ?)"
                        )
                        for (line in lines) {
                            if (line.isBlank() || line.startsWith("#")) continue
                            val parts = line.split("|", limit = 3)
                            if (parts.size < 3) continue
                            stmt.clearBindings()
                            stmt.bindString(1, parts[0].trim())
                            stmt.bindString(2, parts[1].trim())
                            stmt.bindString(3, parts[2].trim())
                            stmt.executeInsert()
                        }
                        db.setTransactionSuccessful()
                    } finally {
                        db.endTransaction()
                    }
                }
            } catch (e: Exception) {
                // Asset missing/unreadable — vault stays empty until content is added.
            }
        }

        companion object {
            private const val DB_NAME = "heritage.db"
            private const val DB_VERSION = 1
            private const val ASSET_NAME = "proverbs_web.txt"
        }
    }
}

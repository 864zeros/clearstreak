package com.eight64zeros.clearstreak.data

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.eight64zeros.clearstreak.model.Verse

/**
 * Offline Heritage Vault store (blueprint §3). A plaintext, non-sensitive SQLite
 * database of public-domain content, seeded on first run from the bundled asset
 * `proverbs_web.txt` (World English Bible, public domain).
 *
 * Uses a PLAIN TABLE + LIKE search rather than FTS5: Android's system SQLite does
 * not reliably include the FTS5 module across devices (creating the virtual table
 * crashed on-device). For a single book (~915 verses) LIKE is instant and cannot
 * fail on a missing module. Every DB access is wrapped so a storage problem
 * degrades to empty results instead of crashing the screen.
 *
 * The 1939 AA Big Book is deliberately excluded — copyright contested / counsel-
 * gated (see PROGRESS.md §5).
 */
class HeritageStore(context: Context) {

    private val helper = HeritageDbHelper(context.applicationContext)

    fun proverbsForChapter(chapter: Int): List<Verse> = try {
        helper.readableDatabase.rawQuery(
            "SELECT chapter, verse, text FROM proverbs WHERE chapter = ? ORDER BY verse",
            arrayOf(chapter.toString())
        ).toVerses()
    } catch (e: Exception) {
        emptyList()
    }

    fun search(raw: String): List<Verse> {
        val words = raw.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        if (words.isEmpty()) return emptyList()
        val where = words.joinToString(" AND ") { "text LIKE ? ESCAPE '\\'" }
        val args = words.map { w ->
            "%" + w.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%"
        }.toTypedArray()
        return try {
            helper.readableDatabase.rawQuery(
                "SELECT chapter, verse, text FROM proverbs WHERE $where ORDER BY chapter, verse LIMIT 50",
                args
            ).toVerses()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun Cursor.toVerses(): List<Verse> {
        val list = mutableListOf<Verse>()
        use {
            while (it.moveToNext()) {
                list.add(Verse(chapter = it.getInt(0), verse = it.getInt(1), text = it.getString(2)))
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
            db.execSQL("CREATE TABLE proverbs (chapter INTEGER, verse INTEGER, text TEXT)")
            db.execSQL("CREATE INDEX idx_proverbs_chapter ON proverbs(chapter)")
            seedFromAsset(db)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            // v1 used an FTS5 virtual table (unreliable on some devices); recreate as a plain table.
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
                            val ch = parts[0].trim().toIntOrNull() ?: continue
                            val vs = parts[1].trim().toIntOrNull() ?: continue
                            stmt.clearBindings()
                            stmt.bindLong(1, ch.toLong())
                            stmt.bindLong(2, vs.toLong())
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
            private const val DB_VERSION = 2
            private const val ASSET_NAME = "proverbs_web.txt"
        }
    }
}

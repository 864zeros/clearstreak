package com.eight64zeros.clearstreak.data

import android.content.Context
import com.eight64zeros.clearstreak.model.DailyVerse
import org.json.JSONArray
import java.time.LocalDate
import kotlin.random.Random

/**
 * Offline Heritage Vault (blueprint §3). Loads the bundled public-domain (KJV)
 * daily-verse set into memory — 365 small records, so no database is needed
 * (and none of the SQLite/FTS fragility). Serves a perpetual verse-of-the-day
 * by day-of-year, plus a random verse.
 *
 * The 1939 AA Big Book is deliberately excluded — copyright contested /
 * counsel-gated (see PROGRESS.md §5).
 */
class HeritageStore(context: Context) {

    val verses: List<DailyVerse> = load(context.applicationContext)

    private fun load(context: Context): List<DailyVerse> = try {
        val json = context.assets.open(ASSET_NAME).bufferedReader().use { it.readText() }
        val arr = JSONArray(json)
        buildList {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                add(
                    DailyVerse(
                        day = o.getInt("day"),
                        emotion = o.optString("emotion", "GENERAL"),
                        citation = o.getString("citation"),
                        text = o.getString("text")
                    )
                )
            }
        }.sortedBy { it.day }
    } catch (e: Exception) {
        emptyList()
    }

    /** Verse for a 1-based day-of-year, wrapping (so Dec 31 on a leap year is safe). */
    fun verseForDayOfYear(dayOfYear: Int): DailyVerse? {
        if (verses.isEmpty()) return null
        val idx = ((dayOfYear - 1) % verses.size + verses.size) % verses.size
        return verses[idx]
    }

    fun verseForDate(date: LocalDate): DailyVerse? = verseForDayOfYear(date.dayOfYear)

    fun randomVerse(): DailyVerse? =
        if (verses.isEmpty()) null else verses[Random.nextInt(verses.size)]

    companion object {
        private const val ASSET_NAME = "daily_verses.json"

        const val SERENITY_PRAYER =
            "God, grant me the serenity to accept the things I cannot change, " +
                "the courage to change the things I can, and the wisdom to know the difference."
    }
}

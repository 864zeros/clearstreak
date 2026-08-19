package com.eight64zeros.clearstreak.data

import android.content.Context
import com.eight64zeros.clearstreak.model.BookPassage
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

/**
 * In-memory store for the moment-of-need passages (original re-authored content
 * from the public-domain 1939 Big Book). Loads the bundled `passages.json` once
 * — 130 small records, so no database is needed. Serves a random passage and,
 * via the taxonomy routing, passages matched to the user's live state.
 */
class PassageStore(context: Context) {

    val passages: List<BookPassage> = load(context.applicationContext)

    private fun load(context: Context): List<BookPassage> = try {
        val text = context.assets.open(ASSET).bufferedReader().use { it.readText() }
        val arr = JSONObject(text).getJSONArray("passages")
        buildList {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val labels = o.getJSONObject("labels")
                val src = o.getJSONObject("source")
                add(
                    BookPassage(
                        id = o.getString("id"),
                        readerText = o.getString("reader_text"),
                        surfaceText = o.getString("surface_text"),
                        faithOptional = if (o.isNull("faith_optional")) null
                        else o.optString("faith_optional").ifBlank { null },
                        moments = labels.getJSONArray("moment").toStringList(),
                        halt = labels.optString("halt", "GENERAL"),
                        urgeLevel = labels.optString("urge_level", "PASSING"),
                        stage = labels.optString("stage", ""),
                        function = labels.optString("function", ""),
                        appliesTo = o.getJSONArray("applies_to").toStringList(),
                        readingTime = o.optString("reading_time", "medium"),
                        chapterTitle = src.optString("chapter_title", ""),
                        page = src.optInt("page", 0)
                    )
                )
            }
        }
    } catch (e: Exception) {
        emptyList()
    }

    fun random(): BookPassage? =
        if (passages.isEmpty()) null else passages[Random.nextInt(passages.size)]

    fun forMoment(moment: String): List<BookPassage> =
        passages.filter { moment in it.moments }

    /** Candidate passages for the live state (urge tier × HALT), via taxonomy routing. */
    fun forState(urgeLevel: String, halt: String): List<BookPassage> {
        val moments = ROUTING[urgeLevel]?.let { it[halt] ?: it["ANY"] } ?: return emptyList()
        return passages.filter { p -> p.moments.any { it in moments } }
    }

    /** One passage for the live state (random among matches; falls back to any). */
    fun oneForState(urgeLevel: String, halt: String): BookPassage? {
        val matches = forState(urgeLevel, halt)
        return if (matches.isEmpty()) random() else matches[Random.nextInt(matches.size)]
    }

    private fun JSONArray.toStringList(): List<String> =
        buildList { for (i in 0 until length()) add(getString(i)) }

    companion object {
        private const val ASSET = "passages.json"

        // Mirrors taxonomy.json routing: urge_level -> HALT -> candidate moments.
        private val ROUTING: Map<String, Map<String, List<String>>> = mapOf(
            "CRITICAL" to mapOf("ANY" to listOf("craving-now", "fear")),
            "WHITE_KNUCKLING" to mapOf(
                "ANGRY" to listOf("resentment"),
                "LONELY" to listOf("lonely", "helping-others"),
                "HOPELESS" to listOf("after-a-slip", "staying-the-course"),
                "STRESSED" to listOf("fear"),
                "TIRED" to listOf("craving-now"),
                "HUNGRY" to listOf("craving-now"),
                "GENERAL" to listOf("craving-now")
            ),
            "PASSING" to mapOf("ANY" to listOf("craving-now", "is-this-me")),
            "CLEAR" to mapOf("ANY" to listOf("staying-the-course", "starting-out", "helping-others"))
        )
    }
}

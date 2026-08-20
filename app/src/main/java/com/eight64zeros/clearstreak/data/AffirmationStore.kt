package com.eight64zeros.clearstreak.data

import android.content.Context
import com.eight64zeros.clearstreak.model.Affirmation
import kotlin.random.Random
import org.json.JSONArray
import org.json.JSONObject

/**
 * In-memory store for in-app affirmations (Home bell banner). Loads the two bundled pillar files
 * once — small records, so no database is needed.
 *
 *  - `affirmations_recovery.json`  — source: `{ book, chapter_title, page, ... }` (Big Book, PD)
 *  - `affirmations_spiritual.json` — source: `{ citation, verse_text }` (KJV, PD)
 *
 * Spiritual (faith) lines are only served when the caller passes `includeFaith = true`
 * (i.e. the user enabled faith reflections).
 */
class AffirmationStore(context: Context) {

    val affirmations: List<Affirmation> =
        parseRecovery(context.applicationContext) + parseSpiritual(context.applicationContext)

    private fun parseRecovery(context: Context): List<Affirmation> = try {
        val arr = readArray(context, RECOVERY_ASSET)
        buildList {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val src = o.optJSONObject("source")
                val chapter = src?.optString("chapter_title", "").orEmpty()
                val page = src?.optInt("page", 0) ?: 0
                val citation = buildString {
                    append("Alcoholics Anonymous (1939)")
                    if (chapter.isNotBlank()) append(" · ").append(chapter)
                    if (page > 0) append(", p.").append(page)
                }
                add(
                    Affirmation(
                        id = o.getString("id"),
                        text = o.getString("text"),
                        pillar = Affirmation.PILLAR_RECOVERY,
                        citation = citation
                    )
                )
            }
        }
    } catch (e: Exception) {
        emptyList()
    }

    private fun parseSpiritual(context: Context): List<Affirmation> = try {
        val arr = readArray(context, SPIRITUAL_ASSET)
        buildList {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val src = o.optJSONObject("source")
                val cite = src?.optString("citation", "").orEmpty()
                add(
                    Affirmation(
                        id = o.getString("id"),
                        text = o.getString("text"),
                        pillar = Affirmation.PILLAR_SPIRITUAL,
                        citation = if (cite.isNotBlank()) "$cite (KJV)" else "KJV",
                        scriptureText = src?.optString("verse_text", "")?.ifBlank { null }
                    )
                )
            }
        }
    } catch (e: Exception) {
        emptyList()
    }

    private fun readArray(context: Context, asset: String): JSONArray =
        JSONObject(context.assets.open(asset).bufferedReader().use { it.readText() })
            .getJSONArray("affirmations")

    private fun pool(includeFaith: Boolean): List<Affirmation> =
        if (includeFaith) affirmations else affirmations.filter { !it.isFaith }

    /** A random affirmation, honoring the faith setting. Null if none are available. */
    fun random(includeFaith: Boolean): Affirmation? {
        val p = pool(includeFaith)
        return if (p.isEmpty()) null else p[Random.nextInt(p.size)]
    }

    companion object {
        private const val RECOVERY_ASSET = "affirmations_recovery.json"
        private const val SPIRITUAL_ASSET = "affirmations_spiritual.json"
    }
}

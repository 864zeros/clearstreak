package com.eight64zeros.clearstreak.data

import android.content.Context
import com.eight64zeros.clearstreak.model.Affirmation
import kotlin.random.Random
import org.json.JSONObject

/**
 * In-memory store for in-app affirmations (short encouragements shown in the Home bell banner).
 * Loads the bundled `affirmations.json` once — small records, so no database is needed.
 * Mirrors [PassageStore]. Faith-tagged affirmations are only served when the user has faith
 * reflections enabled.
 *
 * Expected `affirmations.json` shape (mirrors the passages pipeline; provenance in `source`):
 * ```
 * {
 *   "version": 1,
 *   "count": 42,
 *   "affirmations": [
 *     {
 *       "id": "aff-001",
 *       "text": "You are awesome no matter what happens today.",
 *       "faith": false,
 *       "source": { "label": "864zeros original", "reference": "" }
 *     },
 *     {
 *       "id": "aff-014",
 *       "text": "Just for today, you can do the next right thing.",
 *       "faith": false,
 *       "source": { "label": "Adapted from Alcoholics Anonymous (1939)", "reference": "p.86" }
 *     },
 *     {
 *       "id": "aff-030",
 *       "text": "You are held; you can rest in that.",
 *       "faith": true,
 *       "source": { "label": "Inspired by Psalms", "reference": "46:1" }
 *     }
 *   ]
 * }
 * ```
 */
class AffirmationStore(context: Context) {

    val affirmations: List<Affirmation> = load(context.applicationContext)

    private fun load(context: Context): List<Affirmation> = try {
        val text = context.assets.open(ASSET).bufferedReader().use { it.readText() }
        val arr = JSONObject(text).getJSONArray("affirmations")
        buildList {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val src = o.optJSONObject("source")
                add(
                    Affirmation(
                        id = o.getString("id"),
                        text = o.getString("text"),
                        faith = o.optBoolean("faith", false),
                        sourceLabel = src?.optString("label", "") ?: "",
                        reference = src?.optString("reference", "") ?: ""
                    )
                )
            }
        }
    } catch (e: Exception) {
        emptyList()
    }

    private fun pool(includeFaith: Boolean): List<Affirmation> =
        if (includeFaith) affirmations else affirmations.filter { !it.faith }

    /** A random affirmation, honoring the faith setting. Null if none are available. */
    fun random(includeFaith: Boolean): Affirmation? {
        val p = pool(includeFaith)
        return if (p.isEmpty()) null else p[Random.nextInt(p.size)]
    }

    companion object {
        private const val ASSET = "affirmations.json"
    }
}

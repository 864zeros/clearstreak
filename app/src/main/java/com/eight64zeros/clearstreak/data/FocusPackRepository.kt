package com.eight64zeros.clearstreak.data

import android.content.Context
import com.eight64zeros.clearstreak.model.FocusPackEntry
import com.eight64zeros.clearstreak.model.FocusPacks
import org.json.JSONObject
import java.time.LocalDate
import kotlin.random.Random

/**
 * Compartmentalized Focus Pack repository (`focus_packs.json` + bridges to
 * `HeritageStore`, `PassageStore`, and `AffirmationStore`).
 *
 * Ensures that each philosophical or spiritual worldview remains cleanly separated
 * and serves a structured `{ citation, text, plain, application }` card for any date.
 */
class FocusPackRepository(
    context: Context,
    private val heritageStore: HeritageStore = HeritageStore(context),
    private val passageStore: PassageStore = PassageStore(context),
    private val affirmationStore: AffirmationStore = AffirmationStore(context)
) {

    private val classicalPacks: Map<String, List<FocusPackEntry>> =
        loadClassicalPacks(context.applicationContext)

    private fun loadClassicalPacks(context: Context): Map<String, List<FocusPackEntry>> = try {
        val raw = context.assets.open(ASSET_NAME).bufferedReader().use { it.readText() }
        val root = JSONObject(raw)
        val packsArr = root.getJSONArray("packs")
        buildMap {
            for (i in 0 until packsArr.length()) {
                val packObj = packsArr.getJSONObject(i)
                val packId = packObj.getString("id")
                val packTitle = packObj.getString("title")
                val entriesArr = packObj.getJSONArray("entries")
                val entries = buildList {
                    for (j in 0 until entriesArr.length()) {
                        val e = entriesArr.getJSONObject(j)
                        add(
                            FocusPackEntry(
                                id = e.getString("id"),
                                packId = packId,
                                packTitle = packTitle,
                                citation = e.getString("citation"),
                                text = e.getString("text"),
                                plain = e.getString("plain"),
                                application = e.getString("application")
                            )
                        )
                    }
                }
                put(packId, entries)
            }
        }
    } catch (e: Exception) {
        emptyMap()
    }

    fun entriesForPack(packId: String, includeFaith: Boolean = false): List<FocusPackEntry> {
        val meta = FocusPacks.metaFor(packId)
        return when (packId) {
            FocusPacks.KJV_SCRIPTURE -> heritageStore.verses.map { v ->
                FocusPackEntry(
                    id = "kjv_${v.day}",
                    packId = FocusPacks.KJV_SCRIPTURE,
                    packTitle = meta.title,
                    citation = "${v.citation} (KJV 1611, PD)",
                    text = v.text,
                    plain = "Steadfast strength for today's walk — anchor your attention in what endures beyond the passing moment.",
                    application = "Carry this verse into your Primary Focus check-in and let today's discipline stand firm."
                )
            }
            FocusPacks.RECOVERY_REFLECTIONS -> passageStore.passages.map { p ->
                FocusPackEntry(
                    id = p.id,
                    packId = FocusPacks.RECOVERY_REFLECTIONS,
                    packTitle = meta.title,
                    citation = p.citation,
                    text = p.surfaceText,
                    plain = if (includeFaith && !p.faithOptional.isNullOrBlank()) p.faithOptional else p.readerText,
                    application = "Record an honest check-in today — every clear day is permanently preserved under Data Over Shame."
                )
            }
            FocusPacks.DAILY_AFFIRMATIONS -> {
                val filtered = if (includeFaith) {
                    affirmationStore.affirmations
                } else {
                    affirmationStore.affirmations.filter { it.pillar == com.eight64zeros.clearstreak.model.Affirmation.PILLAR_RECOVERY }
                }
                filtered.map { a ->
                    FocusPackEntry(
                        id = a.id,
                        packId = FocusPacks.DAILY_AFFIRMATIONS,
                        packTitle = meta.title,
                        citation = a.citation,
                        text = a.text,
                        plain = a.scriptureText ?: "A grounded reminder that your past effort is permanent and today's choice is in your hands.",
                        application = "Honor your When–Then Routine Anchor or Floor Rep today to keep your momentum steady."
                    )
                }
            }
            else -> classicalPacks[packId] ?: classicalPacks[FocusPacks.STOIC_MEDITATIONS].orEmpty()
        }
    }

    fun entryForDayOfYear(packId: String, dayOfYear: Int, includeFaith: Boolean = false): FocusPackEntry? {
        val list = entriesForPack(packId, includeFaith)
        if (list.isEmpty()) return null
        val idx = ((dayOfYear - 1) % list.size + list.size) % list.size
        return list[idx]
    }

    fun entryForDate(packId: String, date: LocalDate, includeFaith: Boolean = false): FocusPackEntry? =
        entryForDayOfYear(packId, date.dayOfYear, includeFaith)

    fun randomEntry(packId: String, includeFaith: Boolean = false): FocusPackEntry? {
        val list = entriesForPack(packId, includeFaith)
        return if (list.isEmpty()) null else list[Random.nextInt(list.size)]
    }

    companion object {
        private const val ASSET_NAME = "focus_packs.json"
    }
}

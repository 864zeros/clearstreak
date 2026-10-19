package com.eight64zeros.clearstreak.data

import android.content.Context
import com.eight64zeros.clearstreak.model.FocusPackEntry
import com.eight64zeros.clearstreak.model.FocusPackMeta
import com.eight64zeros.clearstreak.model.FocusPacks
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import kotlin.random.Random

data class ValidatedFocusPack(
    val meta: FocusPackMeta,
    val entries: List<FocusPackEntry>,
    val normalizedJson: String
)

sealed class FocusPackValidationResult {
    data class Valid(
        val pack: ValidatedFocusPack
    ) : FocusPackValidationResult() {
        val packId: String get() = pack.meta.id
        val title: String get() = pack.meta.title
        val subtitle: String get() = pack.meta.subtitle
        val entries: List<FocusPackEntry> get() = pack.entries
    }

    data class Invalid(
        val reason: String
    ) : FocusPackValidationResult()
}

/**
 * Compartmentalized Focus Pack repository (`focus_packs.json` + individual `assets/focus_packs/`
 * + local custom `.json` packs imported via `ACTION_OPEN_DOCUMENT` + bridges to
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

    private val appContext: Context = context.applicationContext
    private val customPacksDir: File = File(appContext.filesDir, CUSTOM_PACKS_DIR)

    private val classicalPacks: Map<String, List<FocusPackEntry>> =
        loadClassicalPacks(appContext)

    private val customPacksMap: MutableMap<String, List<FocusPackEntry>> = LinkedHashMap()
    private val customMetasMap: MutableMap<String, FocusPackMeta> = LinkedHashMap()

    init {
        loadPersistedCustomPacks()
    }

    private fun loadClassicalPacks(context: Context): Map<String, List<FocusPackEntry>> {
        val result = LinkedHashMap<String, List<FocusPackEntry>>()
        try {
            val raw = context.assets.open(ASSET_NAME).bufferedReader().use { it.readText() }
            val root = JSONObject(raw)
            val packsArr = root.getJSONArray("packs")
            for (i in 0 until packsArr.length()) {
                val packObj = packsArr.getJSONObject(i)
                val packId = packObj.optString("id").ifBlank { packObj.optString("pack_id") }
                val packTitle = packObj.optString("title")
                val entriesArr = packObj.getJSONArray("entries")
                val entries = buildList {
                    for (j in 0 until entriesArr.length()) {
                        val e = entriesArr.getJSONObject(j)
                        add(
                            FocusPackEntry(
                                id = e.optString("id").ifBlank { "${packId}_${j + 1}" },
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
                if (packId.isNotBlank() && entries.isNotEmpty()) {
                    result[packId] = entries
                }
            }
        } catch (_: Exception) {
        }

        // Also load individual bundled packs from assets/focus_packs/ if present
        try {
            val assetFiles = context.assets.list(ASSET_PACKS_DIR).orEmpty()
            for (fileName in assetFiles) {
                if (!fileName.endsWith(".json")) continue
                val raw = context.assets.open("$ASSET_PACKS_DIR/$fileName")
                    .bufferedReader()
                    .use { it.readText() }
                val validated = validateFocusPackJson(raw)
                if (validated is FocusPackValidationResult.Valid && !result.containsKey(validated.packId)) {
                    result[validated.packId] = validated.entries
                }
            }
        } catch (_: Exception) {
        }

        return result
    }

    private fun loadPersistedCustomPacks() {
        synchronized(this) {
            customPacksMap.clear()
            customMetasMap.clear()
            if (!customPacksDir.exists() || !customPacksDir.isDirectory) return
            val files = customPacksDir.listFiles { file ->
                file.isFile && file.name.endsWith(".json", ignoreCase = true)
            }?.sortedBy { it.name }.orEmpty()

            for (file in files) {
                try {
                    if (file.length() > MAX_CUSTOM_PACK_BYTES) continue
                    val bytes = file.readBytes()
                    when (val res = validateFocusPackJson(bytes)) {
                        is FocusPackValidationResult.Valid -> {
                            customPacksMap[res.packId] = res.entries
                            customMetasMap[res.packId] = res.pack.meta
                            FocusPacks.registerCustomPack(res.pack.meta)
                        }
                        is FocusPackValidationResult.Invalid -> Unit
                    }
                } catch (_: Exception) {
                }
            }
        }
    }

    fun importCustomPack(rawBytes: ByteArray): FocusPackValidationResult {
        synchronized(this) {
            val result = importCustomPackToDir(rawBytes, customPacksDir)
            if (result is FocusPackValidationResult.Valid) {
                customPacksMap[result.packId] = result.entries
                customMetasMap[result.packId] = result.pack.meta
                FocusPacks.registerCustomPack(result.pack.meta)
            }
            return result
        }
    }

    fun importCustomPack(rawJson: String): FocusPackValidationResult =
        importCustomPack(rawJson.toByteArray(StandardCharsets.UTF_8))

    fun removeCustomPack(packId: String): Boolean {
        synchronized(this) {
            val cleanId = sanitizePackId(packId)
            val removedMem = customPacksMap.remove(cleanId) != null || customMetasMap.remove(cleanId) != null
            FocusPacks.unregisterCustomPack(cleanId)
            val target = File(customPacksDir, "$cleanId.json")
            val removedFile = if (target.exists()) target.delete() else false
            return removedMem || removedFile
        }
    }

    fun customPacks(): List<FocusPackMeta> = synchronized(this) {
        customMetasMap.values.toList()
    }

    fun availablePacks(): List<FocusPackMeta> = FocusPacks.availablePacks()

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
            else -> synchronized(this) {
                customPacksMap[packId]
            } ?: classicalPacks[packId]
                ?: classicalPacks[FocusPacks.STOIC_MEDITATIONS].orEmpty()
        }
    }

    fun entryForDayOfYear(packId: String, dayOfYear: Int, includeFaith: Boolean = false): FocusPackEntry? {
        val list = entriesForPack(packId, includeFaith)
        return entryForDayOfYear(list, dayOfYear)
    }

    fun entryForDate(packId: String, date: LocalDate, includeFaith: Boolean = false): FocusPackEntry? =
        entryForDayOfYear(packId, date.dayOfYear, includeFaith)

    fun randomEntry(packId: String, includeFaith: Boolean = false): FocusPackEntry? {
        val list = entriesForPack(packId, includeFaith)
        return if (list.isEmpty()) null else list[Random.nextInt(list.size)]
    }

    companion object {
        private const val ASSET_NAME = "focus_packs.json"
        private const val ASSET_PACKS_DIR = "focus_packs"
        const val CUSTOM_PACKS_DIR = "custom_focus_packs"
        const val MAX_CUSTOM_PACK_BYTES: Int = 512 * 1024
        const val MAX_PACK_ENTRIES: Int = 366

        private val SCRIPT_BLOCK_REGEX = Regex("(?is)<script\\b[^>]*>.*?</script>")
        private val STYLE_BLOCK_REGEX = Regex("(?is)<style\\b[^>]*>.*?</style>")
        private val HTML_TAG_REGEX = Regex("(?is)<[^>]*>")
        private val ANGLE_BRACKET_REGEX = Regex("[<>]")
        private val JS_SCHEME_REGEX = Regex("(?i)javascript\\s*:")
        private val WHITESPACE_REGEX = Regex("\\s+")
        private val SAFE_ID_CHARS_REGEX = Regex("[^a-zA-Z0-9_-]+")
        private val MULTI_UNDERSCORE_REGEX = Regex("_+")

        /**
         * Strips `<script>...</script>`, `<style>...</style>`, HTML tags `<...>`,
         * `javascript:` URI schemes, and ASCII/ISO control characters.
         */
        fun sanitizeText(raw: String?): String {
            if (raw.isNullOrBlank()) return ""
            var cleaned = raw
                .replace(SCRIPT_BLOCK_REGEX, " ")
                .replace(STYLE_BLOCK_REGEX, " ")
                .replace(HTML_TAG_REGEX, " ")
                .replace(ANGLE_BRACKET_REGEX, "")
                .replace(JS_SCHEME_REGEX, "")

            val sb = StringBuilder(cleaned.length)
            for (ch in cleaned) {
                if (ch.isISOControl()) {
                    sb.append(' ')
                } else {
                    sb.append(ch)
                }
            }
            cleaned = sb.toString().replace(WHITESPACE_REGEX, " ").trim()
            return cleaned
        }

        /**
         * Produces a safe filename/identifier token (`[a-zA-Z0-9_-]`, max 80 chars)
         * after stripping any HTML/script payloads.
         */
        fun sanitizePackId(raw: String?): String {
            val textClean = sanitizeText(raw)
            if (textClean.isBlank()) return ""
            return textClean
                .replace(SAFE_ID_CHARS_REGEX, "_")
                .replace(MULTI_UNDERSCORE_REGEX, "_")
                .trim('_', '-')
                .take(80)
        }

        /**
         * Deterministic 1-based `dayOfYear` entry selector with wrap-around cycling.
         */
        fun entryForDayOfYear(entries: List<FocusPackEntry>, dayOfYear: Int): FocusPackEntry? {
            if (entries.isEmpty()) return null
            val safeDay = if (dayOfYear >= 1) dayOfYear else 1
            val idx = ((safeDay - 1) % entries.size + entries.size) % entries.size
            return entries[idx]
        }

        /**
         * Validates a raw UTF-8 byte array representing a Focus Pack `.json` file
         * without requiring Android `Context`.
         */
        fun validateFocusPackJson(rawBytes: ByteArray): FocusPackValidationResult {
            if (rawBytes.isEmpty()) {
                return FocusPackValidationResult.Invalid("Focus Pack file is empty.")
            }
            if (rawBytes.size > MAX_CUSTOM_PACK_BYTES) {
                return FocusPackValidationResult.Invalid(
                    "Focus Pack file exceeds maximum size of 512 KB (${rawBytes.size} bytes)."
                )
            }
            val rawJson = try {
                String(rawBytes, StandardCharsets.UTF_8)
            } catch (e: Exception) {
                return FocusPackValidationResult.Invalid("Unable to decode UTF-8 JSON payload.")
            }
            return validateFocusPackJson(rawJson, checkByteSize = false)
        }

        /**
         * Validates a JSON string representing a Focus Pack without requiring Android `Context`.
         */
        fun validateFocusPackJson(
            rawJson: String,
            checkByteSize: Boolean = true
        ): FocusPackValidationResult {
            if (checkByteSize) {
                val byteLength = rawJson.toByteArray(StandardCharsets.UTF_8).size
                if (byteLength == 0) {
                    return FocusPackValidationResult.Invalid("Focus Pack JSON is empty.")
                }
                if (byteLength > MAX_CUSTOM_PACK_BYTES) {
                    return FocusPackValidationResult.Invalid(
                        "Focus Pack file exceeds maximum size of 512 KB ($byteLength bytes)."
                    )
                }
            } else if (rawJson.isBlank()) {
                return FocusPackValidationResult.Invalid("Focus Pack JSON is empty.")
            }

            val root = try {
                JSONObject(rawJson)
            } catch (e: Exception) {
                return FocusPackValidationResult.Invalid("Malformed JSON: ${e.message ?: "invalid syntax"}")
            }

            val headerObj = root.optJSONObject("pack_meta") ?: root

            val rawPackId = headerObj.optString("pack_id")
                .ifBlank { headerObj.optString("id") }
                .ifBlank { root.optString("pack_id") }
                .ifBlank { root.optString("id") }
            val packId = sanitizePackId(rawPackId)
            if (packId.isBlank()) {
                return FocusPackValidationResult.Invalid("Missing required header field: 'pack_id' or 'id'.")
            }

            val rawTitle = headerObj.optString("title")
                .ifBlank { root.optString("title") }
            val title = sanitizeText(rawTitle)
            if (title.isBlank()) {
                return FocusPackValidationResult.Invalid("Missing required header field: 'title'.")
            }

            val version = sanitizeText(
                headerObj.optString("version")
                    .ifBlank { headerObj.optString("schema_version") }
                    .ifBlank { root.optString("version") }
                    .ifBlank { root.optString("schema_version") }
            )
            val subtitleRaw = sanitizeText(
                headerObj.optString("subtitle")
                    .ifBlank { headerObj.optString("tradition") }
                    .ifBlank { root.optString("subtitle") }
                    .ifBlank { root.optString("tradition") }
            )
            if (version.isBlank() && subtitleRaw.isBlank()) {
                return FocusPackValidationResult.Invalid(
                    "Missing required header metadata: provide 'version', 'subtitle', or 'tradition'."
                )
            }

            val subtitle = subtitleRaw.ifBlank { "Custom Focus Pack · v$version" }
            val emoji = sanitizeText(
                headerObj.optString("emoji").ifBlank { root.optString("emoji") }
            ).ifBlank { "🧭" }

            val entriesArr = root.optJSONArray("entries")
                ?: return FocusPackValidationResult.Invalid("Missing required 'entries' array.")

            val entryCount = entriesArr.length()
            if (entryCount < 1) {
                return FocusPackValidationResult.Invalid("Focus Pack 'entries' array must contain at least 1 entry.")
            }
            if (entryCount > MAX_PACK_ENTRIES) {
                return FocusPackValidationResult.Invalid(
                    "Focus Pack 'entries' array exceeds maximum of $MAX_PACK_ENTRIES entries ($entryCount found)."
                )
            }

            val validatedEntries = ArrayList<FocusPackEntry>(entryCount)
            val normalizedEntriesJson = JSONArray()

            for (i in 0 until entryCount) {
                val entryObj = entriesArr.optJSONObject(i)
                    ?: return FocusPackValidationResult.Invalid("Entry #${i + 1} is not a valid JSON object.")

                val rawEntryId = entryObj.optString("id").ifBlank {
                    if (entryObj.has("day") && !entryObj.isNull("day")) {
                        val dayStr = sanitizeText(entryObj.opt("day")?.toString())
                        if (dayStr.isNotBlank()) "${packId}_day_$dayStr" else ""
                    } else {
                        ""
                    }
                }
                val entryId = sanitizePackId(rawEntryId)
                if (entryId.isBlank()) {
                    return FocusPackValidationResult.Invalid(
                        "Entry #${i + 1} is missing required field: 'id' or 'day'."
                    )
                }

                val citation = sanitizeText(
                    entryObj.optString("citation").ifBlank { entryObj.optString("reference") }
                )
                if (citation.isBlank()) {
                    return FocusPackValidationResult.Invalid(
                        "Entry #${i + 1} ($entryId) is missing required field: 'citation' or 'reference'."
                    )
                }

                val text = sanitizeText(entryObj.optString("text"))
                if (text.isBlank()) {
                    return FocusPackValidationResult.Invalid(
                        "Entry #${i + 1} ($entryId) is missing required field: 'text'."
                    )
                }

                val plain = sanitizeText(
                    entryObj.optString("plain")
                        .ifBlank { entryObj.optString("reflection_prompt") }
                        .ifBlank { entryObj.optString("strategic_prompt") }
                )
                if (plain.isBlank()) {
                    return FocusPackValidationResult.Invalid(
                        "Entry #${i + 1} ($entryId) is missing required field: 'plain', 'reflection_prompt', or 'strategic_prompt'."
                    )
                }

                val application = sanitizeText(
                    entryObj.optString("application").ifBlank { entryObj.optString("micro_action") }
                )
                if (application.isBlank()) {
                    return FocusPackValidationResult.Invalid(
                        "Entry #${i + 1} ($entryId) is missing required field: 'application' or 'micro_action'."
                    )
                }

                val entry = FocusPackEntry(
                    id = entryId,
                    packId = packId,
                    packTitle = title,
                    citation = citation,
                    text = text,
                    plain = plain,
                    application = application
                )
                validatedEntries.add(entry)

                normalizedEntriesJson.put(
                    JSONObject().apply {
                        put("id", entry.id)
                        put("citation", entry.citation)
                        put("text", entry.text)
                        put("plain", entry.plain)
                        put("application", entry.application)
                    }
                )
            }

            val meta = FocusPackMeta(
                id = packId,
                title = title,
                subtitle = subtitle,
                emoji = emoji,
                isCustom = true
            )

            val normalizedRoot = JSONObject().apply {
                put("pack_id", meta.id)
                put("title", meta.title)
                put("subtitle", meta.subtitle)
                put("version", version.ifBlank { "1.0.0" })
                put("emoji", meta.emoji)
                put("entries", normalizedEntriesJson)
            }

            return FocusPackValidationResult.Valid(
                ValidatedFocusPack(
                    meta = meta,
                    entries = validatedEntries,
                    normalizedJson = normalizedRoot.toString(2)
                )
            )
        }

        /**
         * Validates and persists a custom pack into `targetDir/${sanitizedPackId}.json`.
         * Can be tested directly in JVM unit tests with a temporary directory.
         */
        fun importCustomPackToDir(rawBytes: ByteArray, targetDir: File): FocusPackValidationResult {
            val validation = validateFocusPackJson(rawBytes)
            if (validation is FocusPackValidationResult.Valid) {
                try {
                    if (!targetDir.exists()) {
                        targetDir.mkdirs()
                    }
                    val outFile = File(targetDir, "${validation.packId}.json")
                    outFile.writeText(validation.pack.normalizedJson, StandardCharsets.UTF_8)
                } catch (e: Exception) {
                    return FocusPackValidationResult.Invalid(
                        "Failed to persist custom Focus Pack: ${e.message ?: "I/O error"}"
                    )
                }
            }
            return validation
        }
    }
}


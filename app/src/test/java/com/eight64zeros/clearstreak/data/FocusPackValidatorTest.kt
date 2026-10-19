package com.eight64zeros.clearstreak.data

import com.eight64zeros.clearstreak.model.FocusPacks
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files

class FocusPackValidatorTest {

    @Before
    fun setUp() {
        FocusPacks.clearCustomPacks()
    }

    @Test
    fun `valid Volume 2 seneca_shortness_of_life_30day pack imports cleanly`() {
        val assetFile = File("src/main/assets/focus_packs/seneca_shortness_of_life_30day.json")
            .takeIf { it.exists() }
            ?: File("app/src/main/assets/focus_packs/seneca_shortness_of_life_30day.json")

        assertTrue("Bundled Volume 2 Seneca file should exist in assets/focus_packs", assetFile.exists())

        val result = FocusPackRepository.validateFocusPackJson(assetFile.readBytes())
        assertTrue("Expected Valid result but got $result", result is FocusPackValidationResult.Valid)

        val valid = result as FocusPackValidationResult.Valid
        assertEquals("seneca_shortness_of_life_30day", valid.packId)
        assertTrue(valid.title.contains("Seneca"))
        assertEquals(30, valid.entries.size)
        assertEquals("seneca_sol_001", valid.entries.first().id)
        assertTrue(valid.entries.first().citation.contains("Seneca"))
        assertTrue(valid.entries.first().text.isNotBlank())
        assertTrue(valid.entries.first().plain.isNotBlank())
        assertTrue(valid.entries.first().application.isNotBlank())
    }

    @Test
    fun `all 9 bundled Focus Packs in assets validate and Volume 2 packs are registered in FocusPacks`() {
        val builtInIds = FocusPacks.BUILT_IN.map { it.id }.toSet()
        assertTrue(builtInIds.contains(FocusPacks.SENECA_SHORTNESS_OF_LIFE))
        assertTrue(builtInIds.contains(FocusPacks.EPICTETUS_DISCOURSES))
        assertTrue(builtInIds.contains(FocusPacks.SAMUEL_SMILES_SELF_HELP))
        assertTrue(builtInIds.contains(FocusPacks.ARNOLD_BENNETT_24_HOURS))

        val dir = File("src/main/assets/focus_packs").takeIf { it.exists() }
            ?: File("app/src/main/assets/focus_packs")
        val jsonFiles = dir.listFiles { f -> f.isFile && f.name.endsWith(".json") }.orEmpty()
        assertEquals(9, jsonFiles.size)

        for (file in jsonFiles) {
            val res = FocusPackRepository.validateFocusPackJson(file.readBytes())
            assertTrue("Expected ${file.name} to validate cleanly, got $res", res is FocusPackValidationResult.Valid)
            val valid = res as FocusPackValidationResult.Valid
            assertEquals("Expected 30 entries in ${file.name}", 30, valid.entries.size)
        }
    }

    @Test
    fun `valid custom pack schema with alternative field names and persistence succeeds`() {
        val customJson = """
            {
              "pack_id": "custom_stoic_anchor",
              "title": "Custom Morning Anchor",
              "version": "1.0.0",
              "tradition": "Practical Stoicism",
              "emoji": "🔥",
              "entries": [
                {
                  "day": 1,
                  "reference": "Manual 1.1",
                  "text": "Guard your ruling center at dawn.",
                  "reflection_prompt": "Begin before checking external feeds.",
                  "micro_action": "Take three slow grounding breaths."
                },
                {
                  "id": "custom_02",
                  "citation": "Manual 1.2",
                  "text": "Keep continuity alive on heavy days.",
                  "strategic_prompt": "How can you protect your Floor Rep today?",
                  "application": "Complete a two-minute Floor Rep now."
                }
              ]
            }
        """.trimIndent()

        val tempDir = Files.createTempDirectory("clearstreak_custom_packs_test").toFile()
        try {
            val result = FocusPackRepository.importCustomPackToDir(
                customJson.toByteArray(StandardCharsets.UTF_8),
                tempDir
            )
            assertTrue("Expected Valid result, got $result", result is FocusPackValidationResult.Valid)
            val valid = result as FocusPackValidationResult.Valid
            assertEquals("custom_stoic_anchor", valid.packId)
            assertEquals("Custom Morning Anchor", valid.title)
            assertEquals("Practical Stoicism", valid.subtitle)
            assertEquals(2, valid.entries.size)
            assertEquals("custom_stoic_anchor_day_1", valid.entries[0].id)
            assertEquals("Manual 1.1", valid.entries[0].citation)
            assertEquals("Begin before checking external feeds.", valid.entries[0].plain)
            assertEquals("Take three slow grounding breaths.", valid.entries[0].application)

            val savedFile = File(tempDir, "custom_stoic_anchor.json")
            assertTrue("Persisted custom pack JSON file must exist", savedFile.exists())

            FocusPacks.registerCustomPack(valid.pack.meta)
            assertTrue(FocusPacks.availablePacks().any { it.id == "custom_stoic_anchor" && it.isCustom })
            assertEquals("Custom Morning Anchor", FocusPacks.metaFor("custom_stoic_anchor").title)
        } finally {
            tempDir.deleteRecursively()
        }
    }

    @Test
    fun `rejects oversized payload exceeding 512 KB`() {
        val oversizedBytes = ByteArray(FocusPackRepository.MAX_CUSTOM_PACK_BYTES + 1) { 'a'.code.toByte() }
        val result = FocusPackRepository.validateFocusPackJson(oversizedBytes)
        assertTrue(result is FocusPackValidationResult.Invalid)
        val reason = (result as FocusPackValidationResult.Invalid).reason
        assertTrue("Expected 512 KB size error message, got: $reason", reason.contains("512 KB"))
    }

    @Test
    fun `rejects malformed JSON and missing required header or entry fields`() {
        // 1. Malformed JSON
        val malformed = FocusPackRepository.validateFocusPackJson("{ not valid json")
        assertTrue(malformed is FocusPackValidationResult.Invalid)

        // 2. Missing pack_id / id
        val missingPackId = FocusPackRepository.validateFocusPackJson(
            """
            {
              "title": "No ID Pack",
              "version": "1.0",
              "entries": [
                { "id": "e1", "citation": "c", "text": "t", "plain": "p", "application": "a" }
              ]
            }
            """.trimIndent()
        )
        assertTrue(missingPackId is FocusPackValidationResult.Invalid)
        assertTrue((missingPackId as FocusPackValidationResult.Invalid).reason.contains("pack_id"))

        // 3. Missing version / subtitle / tradition
        val missingVersionAndSubtitle = FocusPackRepository.validateFocusPackJson(
            """
            {
              "pack_id": "missing_meta",
              "title": "Title Only",
              "entries": [
                { "id": "e1", "citation": "c", "text": "t", "plain": "p", "application": "a" }
              ]
            }
            """.trimIndent()
        )
        assertTrue(missingVersionAndSubtitle is FocusPackValidationResult.Invalid)

        // 4. Empty entries array
        val emptyEntries = FocusPackRepository.validateFocusPackJson(
            """
            {
              "pack_id": "empty_pack",
              "title": "Empty Pack",
              "version": "1.0",
              "entries": []
            }
            """.trimIndent()
        )
        assertTrue(emptyEntries is FocusPackValidationResult.Invalid)

        // 5. Entry missing required field (application / micro_action)
        val missingEntryField = FocusPackRepository.validateFocusPackJson(
            """
            {
              "pack_id": "incomplete_entry_pack",
              "title": "Incomplete Entry Pack",
              "version": "1.0",
              "entries": [
                {
                  "id": "e1",
                  "citation": "Seneca",
                  "text": "Valid text",
                  "plain": "Valid plain explanation"
                }
              ]
            }
            """.trimIndent()
        )
        assertTrue(missingEntryField is FocusPackValidationResult.Invalid)
        assertTrue((missingEntryField as FocusPackValidationResult.Invalid).reason.contains("application"))
    }

    @Test
    fun `sanitizes HTML tags, script blocks, javascript schemes, and control characters`() {
        val dirtyJson = """
            {
              "pack_id": "../bad<script>evil()</script>_id!!",
              "title": "<b>Stoic</b> <script type=\"text/javascript\">alert('xss')</script> Clarity",
              "subtitle": "javascript:alert(1) <i>Daily</i>\u0007 Discipline",
              "version": "1.0",
              "entries": [
                {
                  "id": "entry_01<img src=x onerror=alert(1)>",
                  "citation": "<span>Seneca</span> · Epistles",
                  "text": "<script>steal()</script>Hold fast to your <em>ruling center</em>.",
                  "plain": "Ignore <a href=\"javascript:void(0)\">distractions</a> and stay grounded.",
                  "application": "Complete your <strong>Floor Rep</strong> today."
                }
              ]
            }
        """.trimIndent()

        val result = FocusPackRepository.validateFocusPackJson(dirtyJson)
        assertTrue("Expected Valid result after sanitization, got $result", result is FocusPackValidationResult.Valid)
        val valid = result as FocusPackValidationResult.Valid

        assertEquals("bad_id", valid.packId)
        assertEquals("Stoic Clarity", valid.title)
        assertEquals("alert(1) Daily Discipline", valid.subtitle)

        val entry = valid.entries.first()
        assertFalse(entry.id.contains("<"))
        assertEquals("Seneca · Epistles", entry.citation)
        assertEquals("Hold fast to your ruling center .", entry.text.replace("  ", " "))
        assertFalse(entry.text.contains("script", ignoreCase = true))
        assertFalse(entry.text.contains("steal", ignoreCase = true))
        assertFalse(entry.plain.contains("javascript:", ignoreCase = true))
        assertFalse(entry.plain.contains("<a"))
        assertEquals("Complete your Floor Rep today.", entry.application)
    }

    @Test
    fun `deterministic dayOfYear entry cycling wraps cleanly across 365 and 366 days`() {
        val entries = (1..30).map { day ->
            com.eight64zeros.clearstreak.model.FocusPackEntry(
                id = "entry_$day",
                packId = "test_30day",
                packTitle = "Test 30-Day Pack",
                citation = "Citation $day",
                text = "Text $day",
                plain = "Plain $day",
                application = "Application $day"
            )
        }

        val day1 = FocusPackRepository.entryForDayOfYear(entries, 1)
        val day30 = FocusPackRepository.entryForDayOfYear(entries, 30)
        val day31 = FocusPackRepository.entryForDayOfYear(entries, 31)
        val day60 = FocusPackRepository.entryForDayOfYear(entries, 60)
        val day365 = FocusPackRepository.entryForDayOfYear(entries, 365)

        assertNotNull(day1)
        assertEquals("entry_1", day1?.id)
        assertEquals("entry_30", day30?.id)
        assertEquals("entry_1", day31?.id)
        assertEquals("entry_30", day60?.id)
        assertEquals("entry_5", day365?.id)

        // Calling repeatedly with the same dayOfYear is 100% deterministic
        assertEquals(day365, FocusPackRepository.entryForDayOfYear(entries, 365))
    }
}

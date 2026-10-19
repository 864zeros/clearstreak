package com.eight64zeros.clearstreak.model

data class FocusPackMeta(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val isCustom: Boolean = false
)

data class FocusPackEntry(
    val id: String,
    val packId: String,
    val packTitle: String,
    val citation: String,
    val text: String,
    val plain: String,
    val application: String
)

object FocusPacks {
    const val STOIC_MEDITATIONS = "stoic_meditations"
    const val STOIC_MASTERS = "stoic_masters"
    const val FRANKLIN_VIRTUES = "franklin_virtues"
    const val ARISTOTLE_ETHICS = "aristotle_ethics"
    const val JAMES_HABIT = "james_habit"
    const val SENECA_SHORTNESS_OF_LIFE = "seneca_shortness_of_life_30day"
    const val EPICTETUS_DISCOURSES = "epictetus_discourses_30day"
    const val SAMUEL_SMILES_SELF_HELP = "samuel_smiles_self_help_30day"
    const val ARNOLD_BENNETT_24_HOURS = "arnold_bennett_24_hours_30day"
    const val KJV_SCRIPTURE = "kjv_scripture"
    const val RECOVERY_REFLECTIONS = "recovery_reflections"
    const val DAILY_AFFIRMATIONS = "daily_affirmations"

    val BUILT_IN: List<FocusPackMeta> = listOf(
        FocusPackMeta(
            id = STOIC_MEDITATIONS,
            title = "Marcus Aurelius — Meditations",
            subtitle = "Roman Stoic Citadel & Inner Sovereignty (1862/1906 PD)",
            emoji = "🏛️"
        ),
        FocusPackMeta(
            id = STOIC_MASTERS,
            title = "Epictetus & Seneca — Sovereign Will",
            subtitle = "Stoic Command of Assent & Time (1889/1916 PD)",
            emoji = "⚡"
        ),
        FocusPackMeta(
            id = FRANKLIN_VIRTUES,
            title = "Benjamin Franklin — The 13 Virtues",
            subtitle = "Pragmatic Daily Ledger & Sequential Focus (1791 PD)",
            emoji = "📜"
        ),
        FocusPackMeta(
            id = ARISTOTLE_ETHICS,
            title = "Aristotle — Excellence by Repetition",
            subtitle = "Character Forged Through Daily Action (1908 PD)",
            emoji = "⚖️"
        ),
        FocusPackMeta(
            id = JAMES_HABIT,
            title = "William James — The Science of Habit",
            subtitle = "Neuroplasticity & Unbroken Continuity (1890 PD)",
            emoji = "🧠"
        ),
        FocusPackMeta(
            id = SENECA_SHORTNESS_OF_LIFE,
            title = "Seneca — On the Shortness of Life",
            subtitle = "Time Sovereignty & Reclaiming Your Hours (1889 PD · Vol. 2)",
            emoji = "⏳"
        ),
        FocusPackMeta(
            id = EPICTETUS_DISCOURSES,
            title = "Epictetus — The Discourses",
            subtitle = "Dichotomy of Control & Inner Assent (1877 PD · Vol. 2)",
            emoji = "🛡️"
        ),
        FocusPackMeta(
            id = SAMUEL_SMILES_SELF_HELP,
            title = "Samuel Smiles — Self-Help",
            subtitle = "Compounding Industry & Perseverance (1859 PD · Vol. 2)",
            emoji = "🔨"
        ),
        FocusPackMeta(
            id = ARNOLD_BENNETT_24_HOURS,
            title = "Arnold Bennett — 24 Hours a Day",
            subtitle = "Protected Time-Pockets & Anti-Burnout (1910 PD · Vol. 2)",
            emoji = "🕰️"
        ),
        FocusPackMeta(
            id = KJV_SCRIPTURE,
            title = "King James Scripture — 365 Daily Verses",
            subtitle = "Timeless 1611 KJV Wisdom & Strength",
            emoji = "📖"
        ),
        FocusPackMeta(
            id = RECOVERY_REFLECTIONS,
            title = "Sovereign Recovery & Grounding",
            subtitle = "Plain-Language Daily Clarity for Any Journey",
            emoji = "🕊️"
        ),
        FocusPackMeta(
            id = DAILY_AFFIRMATIONS,
            title = "Daily Affirmations & Clarity",
            subtitle = "Direct First-Person Grounding Statements",
            emoji = "✨"
        )
    )

    private val customPackRegistry = LinkedHashMap<String, FocusPackMeta>()

    val ALL: List<FocusPackMeta>
        get() = synchronized(customPackRegistry) {
            BUILT_IN + customPackRegistry.values.filter { custom ->
                BUILT_IN.none { it.id == custom.id }
            }
        }

    fun availablePacks(): List<FocusPackMeta> = ALL

    fun customPacks(): List<FocusPackMeta> = synchronized(customPackRegistry) {
        customPackRegistry.values.toList()
    }

    fun registerCustomPack(meta: FocusPackMeta) {
        synchronized(customPackRegistry) {
            customPackRegistry[meta.id] = meta.copy(isCustom = true)
        }
    }

    fun unregisterCustomPack(packId: String) {
        synchronized(customPackRegistry) {
            customPackRegistry.remove(packId)
        }
    }

    fun clearCustomPacks() {
        synchronized(customPackRegistry) {
            customPackRegistry.clear()
        }
    }

    fun metaFor(packId: String): FocusPackMeta = synchronized(customPackRegistry) {
        customPackRegistry[packId]
            ?: BUILT_IN.firstOrNull { it.id == packId }
            ?: BUILT_IN.first()
    }
}


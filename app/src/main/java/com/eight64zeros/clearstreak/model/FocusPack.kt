package com.eight64zeros.clearstreak.model

data class FocusPackMeta(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String
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
    const val KJV_SCRIPTURE = "kjv_scripture"
    const val RECOVERY_REFLECTIONS = "recovery_reflections"
    const val DAILY_AFFIRMATIONS = "daily_affirmations"

    val ALL: List<FocusPackMeta> = listOf(
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

    fun metaFor(packId: String): FocusPackMeta =
        ALL.firstOrNull { it.id == packId } ?: ALL.first()
}

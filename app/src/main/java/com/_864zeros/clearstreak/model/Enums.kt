package com._864zeros.clearstreak.model

enum class UrgeLevel(val displayName: String, val emoji: String, val levelNumber: Int) {
    CLEAR("Clear", "🟢", 0),
    PASSING("Passing Thought", "🟡", 1),
    WHITE_KNUCKLING("White-Knuckling", "🟠", 2),
    CRITICAL("Critical", "🔴", 3);

    companion object {
        fun fromString(value: String): UrgeLevel {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: CLEAR
        }
    }
}

enum class HaltTrigger(val displayName: String, val emoji: String) {
    HUNGRY("Hungry", "🍏"),
    ANGRY("Angry", "😡"),
    LONELY("Lonely", "👤"),
    TIRED("Tired", "🥱"),
    STRESSED("Stressed", "⚡"),
    HOPELESS("Hopeless", "😞"),
    GENERAL("General", "🛡️");

    companion object {
        fun fromString(value: String?): HaltTrigger {
            if (value == null) return GENERAL
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: GENERAL
        }
    }
}

enum class JourneyCategory(val dbValue: String, val displayName: String) {
    SUBSTANCE("substance", "Substance"),
    BEHAVIORAL("behavioral", "Behavioral"),
    CUSTOM("custom", "Custom");

    companion object {
        fun fromString(value: String): JourneyCategory {
            return entries.firstOrNull { it.dbValue.equals(value, ignoreCase = true) } ?: CUSTOM
        }
    }
}

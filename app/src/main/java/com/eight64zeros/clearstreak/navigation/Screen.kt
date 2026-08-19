package com.eight64zeros.clearstreak.navigation

sealed class Screen(val route: String) {
    data object Lock : Screen("lock")
    data object Dashboard : Screen("dashboard")
    data object Journal : Screen("journal")
    data object Reset : Screen("reset")
    data object Heritage : Screen("heritage")
    data object Settings : Screen("settings")
    data object CrisisIntercept : Screen("crisis_intercept")
    data object AddJourney : Screen("add_journey")
    data class JourneyDetail(val journeyId: String) : Screen("journey_detail/$journeyId")
    data class CheckIn(val journeyId: String) : Screen("check_in/$journeyId")
}

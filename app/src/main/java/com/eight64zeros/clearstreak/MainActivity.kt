package com.eight64zeros.clearstreak

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.eight64zeros.clearstreak.billing.PremiumManager
import com.eight64zeros.clearstreak.billing.computeTrialStatus
import com.eight64zeros.clearstreak.billing.createPremiumManager
import com.eight64zeros.clearstreak.data.AffirmationStore
import com.eight64zeros.clearstreak.data.AppSettingsStorage
import com.eight64zeros.clearstreak.data.EmergencyContactStorage
import com.eight64zeros.clearstreak.data.EmergencyContacts
import com.eight64zeros.clearstreak.data.HeritageStore
import com.eight64zeros.clearstreak.data.PassageStore
import com.eight64zeros.clearstreak.data.SharedStreakStorage
import com.eight64zeros.clearstreak.data.StreakCalculator
import com.eight64zeros.clearstreak.database.DatabaseManager
import com.eight64zeros.clearstreak.model.BookPassage
import com.eight64zeros.clearstreak.model.CheckIn
import com.eight64zeros.clearstreak.model.DailyVerse
import com.eight64zeros.clearstreak.model.Journey
import com.eight64zeros.clearstreak.model.JourneyCategory
import com.eight64zeros.clearstreak.model.UrgeLevel
import com.eight64zeros.clearstreak.navigation.Screen
import com.eight64zeros.clearstreak.review.launchReview
import com.eight64zeros.clearstreak.security.DatabasePassphraseProvider
import com.eight64zeros.clearstreak.security.KeyStoreManager
import com.eight64zeros.clearstreak.ui.screens.AddJourneyModal
import com.eight64zeros.clearstreak.ui.screens.BiometricLockScreen
import com.eight64zeros.clearstreak.ui.screens.CheckInModal
import com.eight64zeros.clearstreak.ui.screens.CrisisInterceptScreen
import com.eight64zeros.clearstreak.ui.screens.DashboardScreen
import com.eight64zeros.clearstreak.ui.screens.GroundingToolsScreen
import com.eight64zeros.clearstreak.ui.screens.HeritageScreen
import com.eight64zeros.clearstreak.ui.screens.JournalScreen
import com.eight64zeros.clearstreak.ui.screens.ScienceScreen
import com.eight64zeros.clearstreak.ui.screens.JourneyDetailScreen
import com.eight64zeros.clearstreak.ui.screens.SettingsScreen
import com.eight64zeros.clearstreak.ui.screens.UnlockScreen
import com.eight64zeros.clearstreak.ui.theme.ClearStreakTheme
import javax.crypto.Cipher

class MainActivity : FragmentActivity() {

    private lateinit var databaseManager: DatabaseManager
    private lateinit var passphraseProvider: DatabasePassphraseProvider
    private lateinit var sharedStorage: SharedStreakStorage
    private lateinit var contactStorage: EmergencyContactStorage
    private lateinit var settingsStorage: AppSettingsStorage
    private lateinit var heritageStore: HeritageStore
    private lateinit var passageStore: PassageStore
    private lateinit var affirmationStore: AffirmationStore
    private lateinit var premiumManager: PremiumManager

    private var isUnlockedState by mutableStateOf(false)
    private var currentRoute by mutableStateOf<String>(Screen.Dashboard.route)
    private var activeJourneyId by mutableStateOf<String?>(null)
    private var authErrorMessage by mutableStateOf<String?>(null)

    // Cached state in memory
    private var journeysState by mutableStateOf<List<Journey>>(emptyList())
    private var checkInsState by mutableStateOf<List<CheckIn>>(emptyList())
    private var contactsState by mutableStateOf(EmergencyContacts())
    private var showVerseOnHome by mutableStateOf(true)
    private var showPassageOnHome by mutableStateOf(false)
    private var showFaithReflections by mutableStateOf(false)
    private var todaysVerse by mutableStateOf<DailyVerse?>(null)
    private var todaysPassage by mutableStateOf<BookPassage?>(null)
    private var currentAffirmation by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Enforce FLAG_SECURE to block screen captures and recent app switcher leaks
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        databaseManager = DatabaseManager(this)
        passphraseProvider = DatabasePassphraseProvider(this)
        sharedStorage = SharedStreakStorage(this)
        contactStorage = EmergencyContactStorage(this)
        settingsStorage = AppSettingsStorage(this)
        heritageStore = HeritageStore(this)
        passageStore = PassageStore(this)
        affirmationStore = AffirmationStore(this)
        premiumManager = createPremiumManager(this)

        contactsState = contactStorage.getContacts()
        showVerseOnHome = settingsStorage.showDailyVerseOnHome
        showPassageOnHome = settingsStorage.showDailyPassageOnHome
        showFaithReflections = settingsStorage.showFaithReflections
        todaysVerse = heritageStore.verseForDate(java.time.LocalDate.now())
        todaysPassage = passageStore.passageForDate(java.time.LocalDate.now())
        currentAffirmation = affirmationStore.random(showFaithReflections)?.text

        setContent {
            ClearStreakTheme {
                MainAppContent()
            }
        }

        // Trigger initial biometric authentication
        authenticateBiometrics()
    }

    private fun authenticateBiometrics() {
        val executor = ContextCompat.getMainExecutor(this)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_prompt_title))
            .setSubtitle(getString(R.string.biometric_prompt_subtitle))
            .setDescription(getString(R.string.biometric_prompt_description))
            .setNegativeButtonText(getString(R.string.biometric_prompt_cancel))
            .build()

        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val cipher = result.cryptoObject?.cipher
                    if (cipher != null) {
                        try {
                            val passphrase = if (!passphraseProvider.isPassphraseInitialized) {
                                passphraseProvider.initializePassphrase(cipher)
                            } else {
                                passphraseProvider.unlockPassphrase(cipher)
                            }
                            databaseManager.unlockEncryptedDatabase(passphrase)
                            isUnlockedState = true
                            authErrorMessage = null
                            loadData()
                        } catch (e: Exception) {
                            authErrorMessage = "Failed to unlock database: ${e.message}"
                        }
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    authErrorMessage = errString.toString()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    authErrorMessage = "Biometric authentication failed. Please try again."
                }
            }
        )

        try {
            val cipher = if (!passphraseProvider.isPassphraseInitialized) {
                KeyStoreManager.getCipherForEncryption()
            } else {
                val iv = passphraseProvider.getStoredIv()
                    ?: throw IllegalStateException("Stored IV not found.")
                KeyStoreManager.getCipherForDecryption(iv)
            }
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        } catch (e: Exception) {
            authErrorMessage = "Security error: ${e.localizedMessage}"
        }
    }

    private fun loadData() {
        val journeys = databaseManager.getJourneys()
        journeysState = journeys
        checkInsState = databaseManager.getAllCheckIns()

        // Sync primary journey to widget storage
        journeys.firstOrNull()?.let { primary ->
            val stats = StreakCalculator.calculateStats(primary, checkInsState)
            sharedStorage.savePrimaryStreakData(
                journeyTitle = primary.title,
                currentStreakDays = stats.currentStreakDays,
                nextMilestoneName = stats.nextMilestoneName,
                nextMilestoneDays = stats.nextMilestoneDays
            )
        }
    }

    private fun lockApp() {
        databaseManager.lockEncryptedDatabase()
        isUnlockedState = false
        currentRoute = Screen.Dashboard.route
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::premiumManager.isInitialized) premiumManager.dispose()
    }

    @Composable
    private fun MainAppContent() {
        if (!isUnlockedState) {
            BiometricLockScreen(
                onAuthenticateClicked = { authenticateBiometrics() },
                errorMessage = authErrorMessage
            )
            return
        }

        val premiumState by premiumManager.state.collectAsState()
        val trialDaysLeft = remember { computeTrialStatus(this@MainActivity).daysRemaining }

        // Free-trial gate: after the trial, the app requires the one-time unlock — but the crisis
        // Rescue hub stays reachable so no one is ever trapped behind a paywall in a hard moment.
        if (!premiumState.isUnlocked && trialDaysLeft <= 0) {
            if (currentRoute == Screen.CrisisIntercept.route) {
                CrisisInterceptScreen(
                    contacts = contactsState,
                    onSafeReturn = { currentRoute = Screen.Unlock.route }
                )
            } else {
                UnlockScreen(
                    state = premiumState,
                    trialDaysRemaining = 0,
                    forced = true,
                    onUnlockClicked = { premiumManager.launchPurchase(this@MainActivity) },
                    onRestoreClicked = { premiumManager.restorePurchases() },
                    onOpenCrisis = { currentRoute = Screen.CrisisIntercept.route },
                    onBack = {}
                )
            }
            return
        }

        when {
            currentRoute == Screen.CrisisIntercept.route -> {
                CrisisInterceptScreen(
                    contacts = contactsState,
                    onSafeReturn = { currentRoute = Screen.Dashboard.route }
                )
            }
            currentRoute == Screen.Journal.route -> {
                JournalScreen(
                    journalEntries = databaseManager.getJournalEntries(),
                    journeys = journeysState,
                    onBack = { currentRoute = Screen.Dashboard.route }
                )
            }
            currentRoute == Screen.Reset.route -> {
                GroundingToolsScreen(
                    showFaith = showFaithReflections,
                    onBack = { currentRoute = Screen.Dashboard.route }
                )
            }
            currentRoute == Screen.Heritage.route -> {
                HeritageScreen(
                    showFaith = showFaithReflections,
                    onBack = { currentRoute = Screen.Dashboard.route }
                )
            }
            currentRoute == Screen.Science.route -> {
                ScienceScreen(
                    onBack = { currentRoute = Screen.Settings.route }
                )
            }
            currentRoute == Screen.Settings.route -> {
                SettingsScreen(
                    contacts = contactsState,
                    onSaveContacts = { updated ->
                        contactStorage.saveContacts(updated)
                        contactsState = updated
                    },
                    showVerseOnHome = showVerseOnHome,
                    onToggleVerseOnHome = {
                        settingsStorage.showDailyVerseOnHome = it
                        showVerseOnHome = it
                    },
                    showPassageOnHome = showPassageOnHome,
                    onTogglePassageOnHome = {
                        settingsStorage.showDailyPassageOnHome = it
                        showPassageOnHome = it
                    },
                    showFaithReflections = showFaithReflections,
                    onToggleFaithReflections = {
                        settingsStorage.showFaithReflections = it
                        showFaithReflections = it
                    },
                    onOpenScience = { currentRoute = Screen.Science.route },
                    isPremiumUnlocked = premiumState.isUnlocked,
                    unlockPriceText = premiumState.priceText,
                    trialDaysRemaining = trialDaysLeft,
                    onOpenUnlock = { currentRoute = Screen.Unlock.route },
                    onRateClicked = { launchReview(this@MainActivity) },
                    onLockApp = { lockApp() },
                    onBack = { currentRoute = Screen.Dashboard.route }
                )
            }
            currentRoute == Screen.Unlock.route -> {
                UnlockScreen(
                    state = premiumState,
                    trialDaysRemaining = trialDaysLeft,
                    forced = false,
                    onUnlockClicked = { premiumManager.launchPurchase(this@MainActivity) },
                    onRestoreClicked = { premiumManager.restorePurchases() },
                    onOpenCrisis = { currentRoute = Screen.CrisisIntercept.route },
                    onBack = { currentRoute = Screen.Settings.route }
                )
            }
            currentRoute == Screen.AddJourney.route -> {
                AddJourneyModal(
                    onAddJourney = { newJourney ->
                        databaseManager.insertJourney(newJourney)
                        loadData()
                        currentRoute = Screen.Dashboard.route
                    },
                    onDismiss = { currentRoute = Screen.Dashboard.route }
                )
            }
            currentRoute.startsWith("check_in/") -> {
                val journey = journeysState.firstOrNull { it.id == activeJourneyId } ?: journeysState.firstOrNull()
                if (journey != null) {
                    CheckInModal(
                        journey = journey,
                        onSaveCheckIn = { checkIn ->
                            databaseManager.insertCheckIn(checkIn)
                            loadData()
                            currentRoute = Screen.Dashboard.route
                        },
                        onTriggerCrisis = {
                            currentRoute = Screen.CrisisIntercept.route
                        },
                        onDismiss = { currentRoute = Screen.Dashboard.route }
                    )
                } else {
                    currentRoute = Screen.Dashboard.route
                }
            }
            currentRoute.startsWith("journey_detail/") -> {
                val journey = journeysState.firstOrNull { it.id == activeJourneyId }
                if (journey != null) {
                    JourneyDetailScreen(
                        journey = journey,
                        checkIns = checkInsState,
                        onCheckInClicked = {
                            activeJourneyId = journey.id
                            currentRoute = "check_in/${journey.id}"
                        },
                        onArchiveClicked = {
                            databaseManager.archiveJourney(journey.id)
                            loadData()
                            currentRoute = Screen.Dashboard.route
                        },
                        onBack = { currentRoute = Screen.Dashboard.route }
                    )
                } else {
                    currentRoute = Screen.Dashboard.route
                }
            }
            else -> {
                DashboardScreen(
                    journeys = journeysState,
                    checkIns = checkInsState,
                    dailyVerse = if (showVerseOnHome) todaysVerse else null,
                    dailyPassage = if (showPassageOnHome) todaysPassage else null,
                    affirmation = currentAffirmation,
                    showFaith = showFaithReflections,
                    onRerollAffirmation = {
                        currentAffirmation = affirmationStore.random(showFaithReflections)?.text
                    },
                    onCheckInClicked = { j ->
                        activeJourneyId = j.id
                        currentRoute = "check_in/${j.id}"
                    },
                    onJourneySelected = { j ->
                        activeJourneyId = j.id
                        currentRoute = "journey_detail/${j.id}"
                    },
                    onAddJourneyClicked = {
                        currentRoute = Screen.AddJourney.route
                    },
                    onCrisisTriggered = {
                        currentRoute = Screen.CrisisIntercept.route
                    },
                    onNavigateJournal = { currentRoute = Screen.Journal.route },
                    onNavigateReset = { currentRoute = Screen.Reset.route },
                    onNavigateHeritage = { currentRoute = Screen.Heritage.route },
                    onNavigateSettings = { currentRoute = Screen.Settings.route },
                    onLockApp = { lockApp() }
                )
            }
        }
    }
}

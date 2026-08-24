package com.eight64zeros.clearstreak

import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
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
import com.eight64zeros.clearstreak.ui.screens.AddJourneyModal
import com.eight64zeros.clearstreak.ui.screens.BiometricLockScreen
import com.eight64zeros.clearstreak.ui.screens.LockMode
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

class MainActivity : FragmentActivity() {

    private lateinit var databaseManager: DatabaseManager
    private lateinit var passphraseProvider: DatabasePassphraseProvider
    private lateinit var sharedStorage: SharedStreakStorage
    private lateinit var contactStorage: EmergencyContactStorage
    private lateinit var settingsStorage: AppSettingsStorage
    private lateinit var heritageStore: HeritageStore
    private lateinit var passageStore: PassageStore
    private lateinit var premiumManager: PremiumManager

    private var isUnlockedState by mutableStateOf(false)
    private var needsDeviceLock by mutableStateOf(false)
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
    private var showAffirmations by mutableStateOf(false)
    private var todaysVerse by mutableStateOf<DailyVerse?>(null)
    private var todaysPassage by mutableStateOf<BookPassage?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Enforce FLAG_SECURE to block screen captures and recent app switcher leaks.
        // The only exception is an opt-in debug capture build (`-Pcapture`) used to shoot store
        // screenshots — release always sets FLAG_SECURE (ALLOW_CAPTURE is false in release).
        if (!BuildConfig.ALLOW_CAPTURE) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }

        databaseManager = DatabaseManager(this)
        passphraseProvider = DatabasePassphraseProvider(this)
        sharedStorage = SharedStreakStorage(this)
        contactStorage = EmergencyContactStorage(this)
        settingsStorage = AppSettingsStorage(this)
        heritageStore = HeritageStore(this)
        passageStore = PassageStore(this)
        premiumManager = createPremiumManager(this)

        contactsState = contactStorage.getContacts()
        showVerseOnHome = settingsStorage.showDailyVerseOnHome
        showPassageOnHome = settingsStorage.showDailyPassageOnHome
        showFaithReflections = settingsStorage.showFaithReflections
        showAffirmations = settingsStorage.showAffirmations
        todaysVerse = heritageStore.verseForDate(java.time.LocalDate.now())
        todaysPassage = passageStore.passageForDate(java.time.LocalDate.now())

        setContent {
            ClearStreakTheme {
                MainAppContent()
            }
        }

        // Kick off the unlock/setup flow
        startAuthFlow()
    }

    override fun onResume() {
        super.onResume()
        // If we sent the user to set up a device lock, re-check when they return.
        if (!isUnlockedState && needsDeviceLock) startAuthFlow()
    }

    /** Entry point: require a device lock, then prompt for biometric OR device credential. */
    private fun startAuthFlow() {
        val authenticators =
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        val status = BiometricManager.from(this).canAuthenticate(authenticators)
        if (status == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            // No biometric AND no PIN/pattern/password — can't create an auth-bound key.
            needsDeviceLock = true
            return
        }
        needsDeviceLock = false
        authenticateBiometrics(authenticators)
    }

    private fun openDeviceLockSettings() {
        try {
            startActivity(Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD))
        } catch (e: Exception) {
            try {
                startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
            } catch (_: Exception) { /* nothing else to do */ }
        }
    }

    private fun authenticateBiometrics(authenticators: Int) {
        val executor = ContextCompat.getMainExecutor(this)
        // DEVICE_CREDENTIAL is the fallback, so no negative ("cancel") button is set.
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_prompt_title))
            .setSubtitle(getString(R.string.biometric_prompt_subtitle))
            .setAllowedAuthenticators(authenticators)
            .build()

        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    try {
                        val passphrase = if (!passphraseProvider.isPassphraseInitialized) {
                            passphraseProvider.initializePassphrase()
                        } else {
                            passphraseProvider.unlockPassphrase()
                        }
                        databaseManager.unlockEncryptedDatabase(passphrase)
                        isUnlockedState = true
                        authErrorMessage = null
                        loadData()
                    } catch (e: KeyPermanentlyInvalidatedException) {
                        authErrorMessage =
                            "Your device security changed, so the encrypted vault can't be opened. " +
                            "If you recently removed your screen lock, restore it and try again."
                    } catch (e: Exception) {
                        authErrorMessage = "Failed to unlock: ${e.message}"
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    authErrorMessage = errString.toString()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    authErrorMessage = "Authentication failed. Please try again."
                }
            }
        )

        try {
            biometricPrompt.authenticate(promptInfo)
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
                mode = when {
                    needsDeviceLock -> LockMode.NEEDS_DEVICE_LOCK
                    !passphraseProvider.isPassphraseInitialized -> LockMode.SETUP
                    else -> LockMode.UNLOCK
                },
                onPrimaryAction = {
                    if (needsDeviceLock) openDeviceLockSettings() else startAuthFlow()
                },
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
                    onNavigate = { currentRoute = it },
                    onBack = { currentRoute = Screen.Dashboard.route }
                )
            }
            currentRoute == Screen.Reset.route -> {
                GroundingToolsScreen(
                    showFaith = showFaithReflections,
                    onNavigate = { currentRoute = it },
                    onBack = { currentRoute = Screen.Dashboard.route }
                )
            }
            currentRoute == Screen.Heritage.route -> {
                HeritageScreen(
                    showFaith = showFaithReflections,
                    showAffirmations = showAffirmations,
                    onNavigate = { currentRoute = it },
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
                    showAffirmations = showAffirmations,
                    onToggleAffirmations = {
                        settingsStorage.showAffirmations = it
                        showAffirmations = it
                    },
                    onOpenScience = { currentRoute = Screen.Science.route },
                    isPremiumUnlocked = premiumState.isUnlocked,
                    unlockPriceText = premiumState.priceText,
                    trialDaysRemaining = trialDaysLeft,
                    onOpenUnlock = { currentRoute = Screen.Unlock.route },
                    onRateClicked = { launchReview(this@MainActivity) },
                    onNavigate = { currentRoute = it },
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
                    showFaith = showFaithReflections,
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
                    onNavigate = { currentRoute = it },
                    onLockApp = { lockApp() }
                )
            }
        }
    }
}

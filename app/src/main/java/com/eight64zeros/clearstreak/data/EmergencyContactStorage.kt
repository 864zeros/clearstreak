package com.eight64zeros.clearstreak.data

import android.content.Context
import android.content.SharedPreferences

data class EmergencyContacts(
    val sponsorName: String = "Sponsor",
    val sponsorPhone: String = "",
    val supportPersonName: String = "Support Person",
    val supportPersonPhone: String = ""
)

class EmergencyContactStorage(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getContacts(): EmergencyContacts {
        return EmergencyContacts(
            sponsorName = prefs.getString(KEY_SPONSOR_NAME, "Sponsor") ?: "Sponsor",
            sponsorPhone = prefs.getString(KEY_SPONSOR_PHONE, "") ?: "",
            supportPersonName = prefs.getString(KEY_SUPPORT_NAME, "Support Person") ?: "Support Person",
            supportPersonPhone = prefs.getString(KEY_SUPPORT_PHONE, "") ?: ""
        )
    }

    fun saveContacts(contacts: EmergencyContacts) {
        prefs.edit()
            .putString(KEY_SPONSOR_NAME, contacts.sponsorName)
            .putString(KEY_SPONSOR_PHONE, contacts.sponsorPhone)
            .putString(KEY_SUPPORT_NAME, contacts.supportPersonName)
            .putString(KEY_SUPPORT_PHONE, contacts.supportPersonPhone)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "clearstreak_emergency_contacts"
        private const val KEY_SPONSOR_NAME = "sponsor_name"
        private const val KEY_SPONSOR_PHONE = "sponsor_phone"
        private const val KEY_SUPPORT_NAME = "support_name"
        private const val KEY_SUPPORT_PHONE = "support_phone"
    }
}

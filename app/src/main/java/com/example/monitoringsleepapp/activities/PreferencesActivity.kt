package com.example.monitoringsleepapp.activities

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.monitoringsleepapp.R
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog

class PreferencesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .commit()
    }

    class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

        private lateinit var sharedPreferences: SharedPreferences

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

            // Register the listener
            sharedPreferences.registerOnSharedPreferenceChangeListener(this)

            // Find the "Clear Journal Data" preference
            val clearJournalDataPreference = findPreference<Preference>("clear_journal_data")
            clearJournalDataPreference?.setOnPreferenceClickListener {
                clearJournalData()
                true
            }

            // Find the "About" preference
            val aboutPreference = findPreference<Preference>("about")
            aboutPreference?.setOnPreferenceClickListener {
                showAboutDialog()
                true
            }

            val sendFeedbackPreference = findPreference<Preference>("feedback_email")
            sendFeedbackPreference?.setOnPreferenceClickListener {
                sendFeedbackEmail()
                true
            }
        }

        private fun showAboutDialog() {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setTitle("About")
            dialogBuilder.setMessage("Monitoring Sleep App\nVersion: 1.0\n\nCredits:\n- Popescu Mihail")
            dialogBuilder.setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = dialogBuilder.create()
            dialog.show()
        }

        private fun sendFeedbackEmail() {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // only email apps should handle this
                putExtra(Intent.EXTRA_EMAIL, arrayOf("flamymind@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Monitoring Sleep App Feedback/Issue")
                putExtra(Intent.EXTRA_TEXT, "Write your feedback or issue here...")
            }

            try {
                startActivity(emailIntent)
            } catch (e: Exception) {
                // Handle error
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            // Unregister the listener
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
            when (key) {
                "theme" -> {
                    // Apply the selected theme
                    applyTheme()
                }
            }
        }

        private fun applyTheme() {
            val themePreference = sharedPreferences.getString("theme", "light")

            when (themePreference) {
                "light" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                "dark" -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        }

        private fun clearJournalData() {
            // Clear all journal data from "JournalPreferences" shared preferences
            val journalPreferences = requireContext().getSharedPreferences("JournalPreferences", AppCompatActivity.MODE_PRIVATE)
            journalPreferences.edit().clear().apply()
        }
    }

}

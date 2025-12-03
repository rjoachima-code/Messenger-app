package com.messenger.app.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial
import com.messenger.app.R
import com.messenger.app.data.model.FontSize
import com.messenger.app.data.repository.SettingsRepository

/**
 * Settings screen for app preferences
 */
class SettingsActivity : AppCompatActivity() {

    // UI Elements
    private lateinit var backButton: ImageButton
    private lateinit var darkModeSwitch: SwitchMaterial
    private lateinit var notificationsSwitch: SwitchMaterial
    private lateinit var soundSwitch: SwitchMaterial
    private lateinit var vibrationSwitch: SwitchMaterial
    private lateinit var previewSwitch: SwitchMaterial
    private lateinit var readReceiptsSwitch: SwitchMaterial
    private lateinit var typingIndicatorSwitch: SwitchMaterial
    private lateinit var lastSeenSwitch: SwitchMaterial
    private lateinit var enterToSendSwitch: SwitchMaterial
    private lateinit var fontSizeSpinner: Spinner
    private lateinit var linkedAccountsSection: LinearLayout
    private lateinit var storageSection: LinearLayout
    private lateinit var aboutSection: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize settings repository
        SettingsRepository.init(this)

        initViews()
        loadSettings()
        setupListeners()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        darkModeSwitch = findViewById(R.id.darkModeSwitch)
        notificationsSwitch = findViewById(R.id.notificationsSwitch)
        soundSwitch = findViewById(R.id.soundSwitch)
        vibrationSwitch = findViewById(R.id.vibrationSwitch)
        previewSwitch = findViewById(R.id.previewSwitch)
        readReceiptsSwitch = findViewById(R.id.readReceiptsSwitch)
        typingIndicatorSwitch = findViewById(R.id.typingIndicatorSwitch)
        lastSeenSwitch = findViewById(R.id.lastSeenSwitch)
        enterToSendSwitch = findViewById(R.id.enterToSendSwitch)
        fontSizeSpinner = findViewById(R.id.fontSizeSpinner)
        linkedAccountsSection = findViewById(R.id.linkedAccountsSection)
        storageSection = findViewById(R.id.storageSection)
        aboutSection = findViewById(R.id.aboutSection)

        // Setup font size spinner
        val fontSizes = FontSize.entries.map { it.displayName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fontSizes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fontSizeSpinner.adapter = adapter
    }

    private fun loadSettings() {
        val settings = SettingsRepository.getSettings()

        darkModeSwitch.isChecked = settings.isDarkMode
        notificationsSwitch.isChecked = settings.notificationsEnabled
        soundSwitch.isChecked = settings.soundEnabled
        vibrationSwitch.isChecked = settings.vibrationEnabled
        previewSwitch.isChecked = settings.showMessagePreview
        readReceiptsSwitch.isChecked = settings.readReceiptsEnabled
        typingIndicatorSwitch.isChecked = settings.typingIndicatorEnabled
        lastSeenSwitch.isChecked = settings.lastSeenVisible
        enterToSendSwitch.isChecked = settings.enterToSend

        // Set font size spinner
        fontSizeSpinner.setSelection(FontSize.entries.indexOf(settings.fontSize))
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            finish()
        }

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            SettingsRepository.setDarkMode(isChecked)
            applyDarkMode(isChecked)
        }

        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            SettingsRepository.setNotificationsEnabled(isChecked)
            updateNotificationDependentSwitches(isChecked)
        }

        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            val settings = SettingsRepository.getSettings()
            SettingsRepository.updateSettings(settings.copy(soundEnabled = isChecked))
        }

        vibrationSwitch.setOnCheckedChangeListener { _, isChecked ->
            val settings = SettingsRepository.getSettings()
            SettingsRepository.updateSettings(settings.copy(vibrationEnabled = isChecked))
        }

        previewSwitch.setOnCheckedChangeListener { _, isChecked ->
            val settings = SettingsRepository.getSettings()
            SettingsRepository.updateSettings(settings.copy(showMessagePreview = isChecked))
        }

        readReceiptsSwitch.setOnCheckedChangeListener { _, isChecked ->
            SettingsRepository.setReadReceiptsEnabled(isChecked)
        }

        typingIndicatorSwitch.setOnCheckedChangeListener { _, isChecked ->
            SettingsRepository.setTypingIndicatorEnabled(isChecked)
        }

        lastSeenSwitch.setOnCheckedChangeListener { _, isChecked ->
            val settings = SettingsRepository.getSettings()
            SettingsRepository.updateSettings(settings.copy(lastSeenVisible = isChecked))
        }

        enterToSendSwitch.setOnCheckedChangeListener { _, isChecked ->
            val settings = SettingsRepository.getSettings()
            SettingsRepository.updateSettings(settings.copy(enterToSend = isChecked))
        }

        fontSizeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSize = FontSize.entries[position]
                SettingsRepository.setFontSize(selectedSize)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        linkedAccountsSection.setOnClickListener {
            Toast.makeText(this, "Manage linked accounts", Toast.LENGTH_SHORT).show()
            // TODO: Open linked accounts screen
        }

        storageSection.setOnClickListener {
            Toast.makeText(this, "Storage management", Toast.LENGTH_SHORT).show()
            // TODO: Open storage management screen
        }

        aboutSection.setOnClickListener {
            Toast.makeText(this, "Messenger App v1.0", Toast.LENGTH_SHORT).show()
            // TODO: Open about screen
        }
    }

    private fun updateNotificationDependentSwitches(enabled: Boolean) {
        soundSwitch.isEnabled = enabled
        vibrationSwitch.isEnabled = enabled
        previewSwitch.isEnabled = enabled

        if (!enabled) {
            soundSwitch.alpha = 0.5f
            vibrationSwitch.alpha = 0.5f
            previewSwitch.alpha = 0.5f
        } else {
            soundSwitch.alpha = 1.0f
            vibrationSwitch.alpha = 1.0f
            previewSwitch.alpha = 1.0f
        }
    }

    private fun applyDarkMode(isDark: Boolean) {
        val mode = if (isDark) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}

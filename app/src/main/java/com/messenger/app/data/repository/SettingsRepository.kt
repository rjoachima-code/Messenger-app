package com.messenger.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.messenger.app.data.model.*

/**
 * Repository for managing user settings
 * Uses SharedPreferences for local storage
 */
object SettingsRepository {
    
    private const val PREFS_NAME = "messenger_settings"
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_FONT_SIZE = "font_size"
    private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    private const val KEY_SOUND_ENABLED = "sound_enabled"
    private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
    private const val KEY_SHOW_PREVIEW = "show_preview"
    private const val KEY_READ_RECEIPTS = "read_receipts"
    private const val KEY_TYPING_INDICATOR = "typing_indicator"
    private const val KEY_LAST_SEEN = "last_seen"
    private const val KEY_PROFILE_PHOTO_VISIBILITY = "profile_photo_visibility"
    private const val KEY_STATUS_VISIBILITY = "status_visibility"
    private const val KEY_AUTO_DOWNLOAD = "auto_download"
    private const val KEY_ENTER_TO_SEND = "enter_to_send"
    private const val KEY_MEDIA_QUALITY = "media_quality"
    private const val KEY_AUTO_PLAY_VOICE = "auto_play_voice"
    private const val KEY_MEDIA_RETENTION = "media_retention"
    private const val KEY_TWO_FACTOR = "two_factor"
    
    private var prefs: SharedPreferences? = null
    private var cachedSettings: UserSettings? = null
    
    /**
     * Initialize the repository with context
     */
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        cachedSettings = loadSettings()
    }
    
    /**
     * Get current user settings
     */
    fun getSettings(): UserSettings {
        return cachedSettings ?: loadSettings()
    }
    
    /**
     * Load settings from SharedPreferences
     */
    private fun loadSettings(): UserSettings {
        val p = prefs ?: return UserSettings()
        
        return UserSettings(
            isDarkMode = p.getBoolean(KEY_DARK_MODE, false),
            fontSize = FontSize.entries.find { it.name == p.getString(KEY_FONT_SIZE, FontSize.MEDIUM.name) } ?: FontSize.MEDIUM,
            notificationsEnabled = p.getBoolean(KEY_NOTIFICATIONS_ENABLED, true),
            soundEnabled = p.getBoolean(KEY_SOUND_ENABLED, true),
            vibrationEnabled = p.getBoolean(KEY_VIBRATION_ENABLED, true),
            showMessagePreview = p.getBoolean(KEY_SHOW_PREVIEW, true),
            readReceiptsEnabled = p.getBoolean(KEY_READ_RECEIPTS, true),
            typingIndicatorEnabled = p.getBoolean(KEY_TYPING_INDICATOR, true),
            lastSeenVisible = p.getBoolean(KEY_LAST_SEEN, true),
            profilePhotoVisible = ProfileVisibility.entries.find { it.name == p.getString(KEY_PROFILE_PHOTO_VISIBILITY, ProfileVisibility.EVERYONE.name) } ?: ProfileVisibility.EVERYONE,
            statusVisible = ProfileVisibility.entries.find { it.name == p.getString(KEY_STATUS_VISIBILITY, ProfileVisibility.EVERYONE.name) } ?: ProfileVisibility.EVERYONE,
            autoDownloadMedia = AutoDownloadSetting.entries.find { it.name == p.getString(KEY_AUTO_DOWNLOAD, AutoDownloadSetting.WIFI_ONLY.name) } ?: AutoDownloadSetting.WIFI_ONLY,
            enterToSend = p.getBoolean(KEY_ENTER_TO_SEND, false),
            mediaQuality = MediaQuality.entries.find { it.name == p.getString(KEY_MEDIA_QUALITY, MediaQuality.STANDARD.name) } ?: MediaQuality.STANDARD,
            autoPlayVoiceMessages = p.getBoolean(KEY_AUTO_PLAY_VOICE, true),
            keepMediaFor = MediaRetention.entries.find { it.name == p.getString(KEY_MEDIA_RETENTION, MediaRetention.FOREVER.name) } ?: MediaRetention.FOREVER,
            twoFactorEnabled = p.getBoolean(KEY_TWO_FACTOR, false)
        )
    }
    
    /**
     * Update settings
     */
    fun updateSettings(settings: UserSettings) {
        cachedSettings = settings
        prefs?.edit()?.apply {
            putBoolean(KEY_DARK_MODE, settings.isDarkMode)
            putString(KEY_FONT_SIZE, settings.fontSize.name)
            putBoolean(KEY_NOTIFICATIONS_ENABLED, settings.notificationsEnabled)
            putBoolean(KEY_SOUND_ENABLED, settings.soundEnabled)
            putBoolean(KEY_VIBRATION_ENABLED, settings.vibrationEnabled)
            putBoolean(KEY_SHOW_PREVIEW, settings.showMessagePreview)
            putBoolean(KEY_READ_RECEIPTS, settings.readReceiptsEnabled)
            putBoolean(KEY_TYPING_INDICATOR, settings.typingIndicatorEnabled)
            putBoolean(KEY_LAST_SEEN, settings.lastSeenVisible)
            putString(KEY_PROFILE_PHOTO_VISIBILITY, settings.profilePhotoVisible.name)
            putString(KEY_STATUS_VISIBILITY, settings.statusVisible.name)
            putString(KEY_AUTO_DOWNLOAD, settings.autoDownloadMedia.name)
            putBoolean(KEY_ENTER_TO_SEND, settings.enterToSend)
            putString(KEY_MEDIA_QUALITY, settings.mediaQuality.name)
            putBoolean(KEY_AUTO_PLAY_VOICE, settings.autoPlayVoiceMessages)
            putString(KEY_MEDIA_RETENTION, settings.keepMediaFor.name)
            putBoolean(KEY_TWO_FACTOR, settings.twoFactorEnabled)
            apply()
        }
    }
    
    /**
     * Toggle dark mode
     */
    fun setDarkMode(enabled: Boolean) {
        cachedSettings = getSettings().copy(isDarkMode = enabled)
        prefs?.edit()?.putBoolean(KEY_DARK_MODE, enabled)?.apply()
    }
    
    /**
     * Check if dark mode is enabled
     */
    fun isDarkMode(): Boolean = getSettings().isDarkMode
    
    /**
     * Toggle notifications
     */
    fun setNotificationsEnabled(enabled: Boolean) {
        cachedSettings = getSettings().copy(notificationsEnabled = enabled)
        prefs?.edit()?.putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)?.apply()
    }
    
    /**
     * Toggle read receipts
     */
    fun setReadReceiptsEnabled(enabled: Boolean) {
        cachedSettings = getSettings().copy(readReceiptsEnabled = enabled)
        prefs?.edit()?.putBoolean(KEY_READ_RECEIPTS, enabled)?.apply()
    }
    
    /**
     * Toggle typing indicator
     */
    fun setTypingIndicatorEnabled(enabled: Boolean) {
        cachedSettings = getSettings().copy(typingIndicatorEnabled = enabled)
        prefs?.edit()?.putBoolean(KEY_TYPING_INDICATOR, enabled)?.apply()
    }
    
    /**
     * Set font size
     */
    fun setFontSize(size: FontSize) {
        cachedSettings = getSettings().copy(fontSize = size)
        prefs?.edit()?.putString(KEY_FONT_SIZE, size.name)?.apply()
    }
    
    /**
     * Clear all settings (reset to defaults)
     */
    fun clearSettings() {
        prefs?.edit()?.clear()?.apply()
        cachedSettings = UserSettings()
    }
}

package com.messenger.app.data.model

/**
 * Data class representing user settings and preferences
 */
data class UserSettings(
    val userId: String = "me",
    
    // Appearance
    val isDarkMode: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM,
    val chatWallpaper: String? = null,
    
    // Notifications
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val showMessagePreview: Boolean = true,
    val notificationTone: String? = null,
    
    // Privacy
    val readReceiptsEnabled: Boolean = true,
    val typingIndicatorEnabled: Boolean = true,
    val lastSeenVisible: Boolean = true,
    val profilePhotoVisible: ProfileVisibility = ProfileVisibility.EVERYONE,
    val statusVisible: ProfileVisibility = ProfileVisibility.EVERYONE,
    
    // Chat
    val autoDownloadMedia: AutoDownloadSetting = AutoDownloadSetting.WIFI_ONLY,
    val enterToSend: Boolean = false,
    val mediaQuality: MediaQuality = MediaQuality.STANDARD,
    val autoPlayVoiceMessages: Boolean = true,
    
    // Storage
    val keepMediaFor: MediaRetention = MediaRetention.FOREVER,
    val clearCacheAutomatically: Boolean = false,
    
    // Account
    val twoFactorEnabled: Boolean = false,
    val linkedAccounts: List<LinkedAccount> = emptyList()
)

/**
 * Font size options
 */
enum class FontSize(val displayName: String, val scale: Float) {
    SMALL("Small", 0.85f),
    MEDIUM("Medium", 1.0f),
    LARGE("Large", 1.15f),
    EXTRA_LARGE("Extra Large", 1.3f)
}

/**
 * Profile visibility options
 */
enum class ProfileVisibility(val displayName: String) {
    EVERYONE("Everyone"),
    CONTACTS("My Contacts"),
    NOBODY("Nobody")
}

/**
 * Auto-download setting options
 */
enum class AutoDownloadSetting(val displayName: String) {
    NEVER("Never"),
    WIFI_ONLY("Wi-Fi only"),
    WIFI_AND_CELLULAR("Wi-Fi and Cellular")
}

/**
 * Media quality options
 */
enum class MediaQuality(val displayName: String) {
    LOW("Low (save data)"),
    STANDARD("Standard"),
    HIGH("High quality")
}

/**
 * Media retention period options
 */
enum class MediaRetention(val displayName: String, val days: Int) {
    ONE_WEEK("1 week", 7),
    ONE_MONTH("1 month", 30),
    THREE_MONTHS("3 months", 90),
    ONE_YEAR("1 year", 365),
    FOREVER("Forever", -1)
}

/**
 * Linked social account
 */
data class LinkedAccount(
    val platform: Platform,
    val username: String,
    val isConnected: Boolean = true,
    val connectedAt: Long = System.currentTimeMillis()
)

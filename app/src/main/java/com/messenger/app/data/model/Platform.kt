package com.messenger.app.data.model

/**
 * Enum representing the different messaging platforms supported
 */
enum class Platform(val displayName: String, val colorHex: String) {
    SMS("SMS", "#34C759"),
    FACEBOOK("Facebook", "#1877F2"),
    INSTAGRAM("Instagram", "#E4405F"),
    TIKTOK("TikTok", "#000000"),
    IMESSAGE("iMessage", "#007AFF")
}

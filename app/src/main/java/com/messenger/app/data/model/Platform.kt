package com.messenger.app.data.model

/**
 * Enum representing the messaging type
 */
enum class Platform(val displayName: String, val colorHex: String) {
    SMS("SMS", "#34C759"),
    MMS("MMS", "#007AFF")
}

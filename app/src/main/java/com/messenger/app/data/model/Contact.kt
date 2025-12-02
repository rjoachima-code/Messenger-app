package com.messenger.app.data.model

/**
 * Data class representing a contact/user
 */
data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String? = null,
    val email: String? = null,
    val avatarUrl: String? = null,
    val isOnline: Boolean = false,
    val lastSeen: Long? = null,
    val platform: Platform = Platform.SMS
)

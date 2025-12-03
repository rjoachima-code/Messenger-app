package com.messenger.app.data.model

/**
 * Data class representing a conversation in the conversation list
 */
data class Conversation(
    val id: String,
    val contact: Contact,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int = 0,
    val platform: Platform = Platform.SMS,
    val isTyping: Boolean = false,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val isArchived: Boolean = false
) {
    /**
     * Get formatted time for display
     */
    fun getFormattedTime(): String {
        val now = System.currentTimeMillis()
        val diff = now - lastMessageTime
        
        return when {
            diff < 60_000 -> "Now"
            diff < 3600_000 -> "${diff / 60_000}m"
            diff < 86400_000 -> "${diff / 3600_000}h"
            diff < 604800_000 -> "${diff / 86400_000}d"
            else -> {
                val sdf = java.text.SimpleDateFormat("MMM d", java.util.Locale.getDefault())
                sdf.format(java.util.Date(lastMessageTime))
            }
        }
    }
}

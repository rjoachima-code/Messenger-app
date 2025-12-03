package com.messenger.app.data.model

/**
 * Data class representing a conversation in the conversation list
 * Supports both 1-on-1 and group conversations
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
    val isArchived: Boolean = false,
    // Group chat properties
    val isGroup: Boolean = false,
    val groupName: String? = null,
    val groupAvatarUrl: String? = null,
    val participants: List<Contact> = emptyList(),
    val adminIds: List<String> = emptyList(),
    val typingParticipants: List<String> = emptyList()
) {
    /**
     * Get the display name for the conversation
     */
    fun getDisplayName(): String {
        return if (isGroup) {
            groupName ?: participants.take(3).joinToString(", ") { it.name }
        } else {
            contact.name
        }
    }
    
    /**
     * Get initials for group or contact avatar
     */
    fun getAvatarInitials(): String {
        val name = getDisplayName()
        val parts = name.split(" ")
        return when {
            parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}"
            parts.isNotEmpty() -> parts[0].take(2)
            else -> "?"
        }.uppercase()
    }
    
    /**
     * Get participant count for group chats
     */
    fun getParticipantCount(): Int = if (isGroup) participants.size else 1
    
    /**
     * Check if current user is admin (for group chats)
     */
    fun isUserAdmin(userId: String): Boolean = adminIds.contains(userId)
    
    /**
     * Get typing indicator text
     */
    fun getTypingText(): String {
        return if (isGroup && typingParticipants.isNotEmpty()) {
            when (typingParticipants.size) {
                1 -> "${typingParticipants.first()} is typing..."
                2 -> "${typingParticipants[0]} and ${typingParticipants[1]} are typing..."
                else -> "Several people are typing..."
            }
        } else if (isTyping) {
            "typing..."
        } else {
            ""
        }
    }
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

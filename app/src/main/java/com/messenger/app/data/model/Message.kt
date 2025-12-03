package com.messenger.app.data.model

/**
 * Data class representing a message in a conversation
 */
data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val timestamp: Long,
    val type: MessageType = MessageType.TEXT,
    val status: MessageStatus = MessageStatus.SENT,
    val isOutgoing: Boolean = false,
    val mediaUrl: String? = null,
    val voiceDuration: Int? = null, // Duration in seconds for voice messages
    val reactions: List<Reaction> = emptyList(),
    val replyToMessageId: String? = null,
    val replyToContent: String? = null,
    val isSelected: Boolean = false,
    // Forwarding properties
    val isForwarded: Boolean = false,
    val originalSenderId: String? = null,
    val originalSenderName: String? = null,
    val forwardedFrom: String? = null,
    // Editing properties
    val isEdited: Boolean = false,
    val editedAt: Long? = null,
    val originalContent: String? = null,
    // Scheduled message
    val isScheduled: Boolean = false,
    val scheduledTime: Long? = null,
    // Pinned message
    val isPinned: Boolean = false,
    val pinnedAt: Long? = null,
    val pinnedBy: String? = null
) {
    /**
     * Get the reaction count for display
     */
    fun getReactionCount(): Int = reactions.size
    
    /**
     * Get reactions grouped by emoji
     */
    fun getGroupedReactions(): Map<String, List<Reaction>> {
        return reactions.groupBy { it.emoji }
    }
    
    /**
     * Check if user has reacted
     */
    fun hasUserReacted(userId: String): Boolean {
        return reactions.any { it.userId == userId }
    }
    
    /**
     * Get user's reaction
     */
    fun getUserReaction(userId: String): Reaction? {
        return reactions.find { it.userId == userId }
    }
    
    /**
     * Get formatted timestamp for display
     */
    fun getFormattedTime(): String {
        val sdf = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
    
    /**
     * Get forwarded from text
     */
    fun getForwardedText(): String? {
        return if (isForwarded) {
            "Forwarded from ${originalSenderName ?: forwardedFrom ?: "Unknown"}"
        } else null
    }
    
    /**
     * Check if message can be edited (within 15 minutes)
     */
    fun canEdit(): Boolean {
        if (!isOutgoing || type != MessageType.TEXT) return false
        val fifteenMinutes = 15 * 60 * 1000L
        return System.currentTimeMillis() - timestamp < fifteenMinutes
    }
    
    /**
     * Get scheduled time text
     */
    fun getScheduledTimeText(): String? {
        return if (isScheduled && scheduledTime != null) {
            val sdf = java.text.SimpleDateFormat("MMM d, h:mm a", java.util.Locale.getDefault())
            "Scheduled for ${sdf.format(java.util.Date(scheduledTime))}"
        } else null
    }
}

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
    val isSelected: Boolean = false
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
}

package com.messenger.app.data.repository

import com.messenger.app.data.model.*

/**
 * Repository for managing messages - currently using mock data
 * In production, this would connect to SQLite/Room database and real APIs
 */
object MessageRepository {
    
    private val mockContacts = listOf(
        Contact("1", "Sarah Johnson", "+1234567890", avatarUrl = null, isOnline = true, platform = Platform.IMESSAGE),
        Contact("2", "Mike Chen", "+1234567891", avatarUrl = null, isOnline = false, platform = Platform.SMS),
        Contact("3", "Emma Wilson", "+1234567892", avatarUrl = null, isOnline = true, platform = Platform.FACEBOOK),
        Contact("4", "James Brown", "+1234567893", avatarUrl = null, isOnline = false, platform = Platform.INSTAGRAM),
        Contact("5", "Olivia Davis", "+1234567894", avatarUrl = null, isOnline = true, platform = Platform.TIKTOK),
        Contact("6", "William Taylor", "+1234567895", avatarUrl = null, isOnline = false, platform = Platform.SMS),
        Contact("7", "Sophia Martinez", "+1234567896", avatarUrl = null, isOnline = true, platform = Platform.FACEBOOK),
        Contact("8", "Alexander Lee", "+1234567897", avatarUrl = null, isOnline = false, platform = Platform.INSTAGRAM)
    )
    
    private val mockConversations = mutableListOf(
        Conversation("1", mockContacts[0], "Hey! How are you doing?", System.currentTimeMillis() - 60_000, 2, Platform.IMESSAGE, isTyping = true),
        Conversation("2", mockContacts[1], "The meeting is at 3 PM", System.currentTimeMillis() - 3600_000, 0, Platform.SMS),
        Conversation("3", mockContacts[2], "Check out this photo! 📸", System.currentTimeMillis() - 7200_000, 5, Platform.FACEBOOK),
        Conversation("4", mockContacts[3], "Just posted a new story!", System.currentTimeMillis() - 86400_000, 1, Platform.INSTAGRAM),
        Conversation("5", mockContacts[4], "Did you see my latest video?", System.currentTimeMillis() - 172800_000, 0, Platform.TIKTOK),
        Conversation("6", mockContacts[5], "Thanks for helping yesterday", System.currentTimeMillis() - 259200_000, 0, Platform.SMS),
        Conversation("7", mockContacts[6], "Movie night this weekend?", System.currentTimeMillis() - 345600_000, 3, Platform.FACEBOOK),
        Conversation("8", mockContacts[7], "Cool! Let's connect later", System.currentTimeMillis() - 432000_000, 0, Platform.INSTAGRAM)
    )
    
    private val mockMessages = mutableMapOf<String, MutableList<Message>>()
    
    init {
        // Initialize mock messages for each conversation
        mockConversations.forEach { conv ->
            mockMessages[conv.id] = generateMockMessages(conv.id, conv.contact)
        }
    }
    
    private fun generateMockMessages(conversationId: String, contact: Contact): MutableList<Message> {
        val now = System.currentTimeMillis()
        return mutableListOf(
            Message(
                id = "${conversationId}_1",
                conversationId = conversationId,
                senderId = contact.id,
                senderName = contact.name,
                content = "Hey there! 👋",
                timestamp = now - 3600_000,
                isOutgoing = false,
                status = MessageStatus.READ
            ),
            Message(
                id = "${conversationId}_2",
                conversationId = conversationId,
                senderId = "me",
                senderName = "Me",
                content = "Hi! How's it going?",
                timestamp = now - 3500_000,
                isOutgoing = true,
                status = MessageStatus.READ
            ),
            Message(
                id = "${conversationId}_3",
                conversationId = conversationId,
                senderId = contact.id,
                senderName = contact.name,
                content = "Pretty good! Just finished work. What about you?",
                timestamp = now - 3400_000,
                isOutgoing = false,
                status = MessageStatus.READ
            ),
            Message(
                id = "${conversationId}_4",
                conversationId = conversationId,
                senderId = "me",
                senderName = "Me",
                content = "Same here. Want to grab coffee tomorrow?",
                timestamp = now - 3300_000,
                isOutgoing = true,
                status = MessageStatus.DELIVERED,
                reactions = listOf(Reaction("❤️", contact.id, contact.name, now - 3200_000))
            ),
            Message(
                id = "${conversationId}_5",
                conversationId = conversationId,
                senderId = contact.id,
                senderName = contact.name,
                content = "That sounds great! ☕ What time works for you?",
                timestamp = now - 120_000,
                isOutgoing = false,
                status = MessageStatus.READ
            ),
            Message(
                id = "${conversationId}_6",
                conversationId = conversationId,
                senderId = "me",
                senderName = "Me",
                content = "How about 10 AM at the usual place?",
                timestamp = now - 60_000,
                isOutgoing = true,
                status = MessageStatus.SENT
            )
        )
    }
    
    fun getConversations(): List<Conversation> = mockConversations.sortedByDescending { it.lastMessageTime }
    
    fun getConversationsByPlatform(platform: Platform): List<Conversation> {
        return mockConversations.filter { it.platform == platform }.sortedByDescending { it.lastMessageTime }
    }
    
    fun searchConversations(query: String): List<Conversation> {
        val lowerQuery = query.lowercase()
        return mockConversations.filter { 
            it.contact.name.lowercase().contains(lowerQuery) || 
            it.lastMessage.lowercase().contains(lowerQuery)
        }.sortedByDescending { it.lastMessageTime }
    }
    
    fun getConversation(conversationId: String): Conversation? {
        return mockConversations.find { it.id == conversationId }
    }
    
    fun getMessages(conversationId: String): List<Message> {
        return mockMessages[conversationId] ?: emptyList()
    }
    
    fun sendMessage(conversationId: String, content: String, type: MessageType = MessageType.TEXT): Message {
        val message = Message(
            id = "${conversationId}_${System.currentTimeMillis()}",
            conversationId = conversationId,
            senderId = "me",
            senderName = "Me",
            content = content,
            timestamp = System.currentTimeMillis(),
            type = type,
            isOutgoing = true,
            status = MessageStatus.SENDING
        )
        mockMessages.getOrPut(conversationId) { mutableListOf() }.add(message)
        
        // Update conversation last message
        val convIndex = mockConversations.indexOfFirst { it.id == conversationId }
        if (convIndex >= 0) {
            mockConversations[convIndex] = mockConversations[convIndex].copy(
                lastMessage = content,
                lastMessageTime = System.currentTimeMillis()
            )
        }
        
        return message
    }
    
    fun addReaction(messageId: String, conversationId: String, emoji: String): Message? {
        val messages = mockMessages[conversationId] ?: return null
        val messageIndex = messages.indexOfFirst { it.id == messageId }
        if (messageIndex < 0) return null
        
        val message = messages[messageIndex]
        val newReaction = Reaction(emoji, "me", "Me", System.currentTimeMillis())
        val updatedReactions = message.reactions.filter { it.userId != "me" } + newReaction
        val updatedMessage = message.copy(reactions = updatedReactions)
        messages[messageIndex] = updatedMessage
        return updatedMessage
    }
    
    fun removeReaction(messageId: String, conversationId: String): Message? {
        val messages = mockMessages[conversationId] ?: return null
        val messageIndex = messages.indexOfFirst { it.id == messageId }
        if (messageIndex < 0) return null
        
        val message = messages[messageIndex]
        val updatedReactions = message.reactions.filter { it.userId != "me" }
        val updatedMessage = message.copy(reactions = updatedReactions)
        messages[messageIndex] = updatedMessage
        return updatedMessage
    }
    
    fun deleteMessage(messageId: String, conversationId: String): Boolean {
        val messages = mockMessages[conversationId] ?: return false
        return messages.removeIf { it.id == messageId }
    }
    
    fun markConversationAsRead(conversationId: String) {
        val convIndex = mockConversations.indexOfFirst { it.id == conversationId }
        if (convIndex >= 0) {
            mockConversations[convIndex] = mockConversations[convIndex].copy(unreadCount = 0)
        }
    }
}

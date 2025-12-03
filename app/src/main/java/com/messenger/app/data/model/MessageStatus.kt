package com.messenger.app.data.model

/**
 * Enum representing the status of a message
 */
enum class MessageStatus {
    SENDING,    // Message is being sent
    SENT,       // Single check - message sent to server
    DELIVERED,  // Double check - message delivered to recipient
    READ,       // Blue double check - message read by recipient
    FAILED      // Message failed to send
}

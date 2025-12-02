package com.messenger.app.data.model

/**
 * Data class representing a message reaction (tapback)
 */
data class Reaction(
    val emoji: String,
    val userId: String,
    val userName: String,
    val timestamp: Long
) {
    companion object {
        val AVAILABLE_REACTIONS = listOf("❤️", "👍", "😂", "😮", "😢", "😡")
    }
}

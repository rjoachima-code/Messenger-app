package com.messenger.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.messenger.app.R
import com.messenger.app.data.repository.SettingsRepository
import com.messenger.app.ui.chat.ChatActivity

/**
 * Service for managing push notifications
 * In production, this would integrate with Firebase Cloud Messaging
 */
object NotificationService {

    private const val CHANNEL_ID_MESSAGES = "messages"
    private const val CHANNEL_ID_CALLS = "calls"
    private const val CHANNEL_NAME_MESSAGES = "Messages"
    private const val CHANNEL_NAME_CALLS = "Calls"

    /**
     * Initialize notification channels (required for Android 8.0+)
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)

            // Messages channel
            val messagesChannel = NotificationChannel(
                CHANNEL_ID_MESSAGES,
                CHANNEL_NAME_MESSAGES,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "New message notifications"
                enableVibration(true)
                enableLights(true)
            }

            // Calls channel
            val callsChannel = NotificationChannel(
                CHANNEL_ID_CALLS,
                CHANNEL_NAME_CALLS,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Incoming call notifications"
                enableVibration(true)
                enableLights(true)
            }

            notificationManager.createNotificationChannels(listOf(messagesChannel, callsChannel))
        }
    }

    /**
     * Show a notification for a new message
     */
    fun showMessageNotification(
        context: Context,
        conversationId: String,
        senderName: String,
        messageContent: String,
        notificationId: Int = senderName.hashCode()
    ) {
        val settings = SettingsRepository.getSettings()
        
        // Check if notifications are enabled
        if (!settings.notificationsEnabled) return

        val notificationManager = context.getSystemService(NotificationManager::class.java)

        // Create intent to open chat
        val intent = Intent(context, ChatActivity::class.java).apply {
            putExtra(ChatActivity.EXTRA_CONVERSATION_ID, conversationId)
            putExtra(ChatActivity.EXTRA_CONTACT_NAME, senderName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID_MESSAGES)
            .setSmallIcon(R.drawable.ic_message)
            .setContentTitle(senderName)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)

        // Show message preview based on settings
        if (settings.showMessagePreview) {
            builder.setContentText(messageContent)
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(messageContent))
        } else {
            builder.setContentText("New message")
        }

        // Add sound if enabled
        if (!settings.soundEnabled) {
            builder.setSilent(true)
        }

        // Add vibration if enabled
        if (settings.vibrationEnabled) {
            builder.setVibrate(longArrayOf(0, 250, 250, 250))
        }

        notificationManager.notify(notificationId, builder.build())
    }

    /**
     * Show a notification for an incoming call
     */
    fun showCallNotification(
        context: Context,
        callerName: String,
        isVideoCall: Boolean,
        notificationId: Int = callerName.hashCode()
    ) {
        val settings = SettingsRepository.getSettings()
        
        if (!settings.notificationsEnabled) return

        val notificationManager = context.getSystemService(NotificationManager::class.java)

        val callType = if (isVideoCall) "Video call" else "Voice call"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_CALLS)
            .setSmallIcon(if (isVideoCall) R.drawable.ic_video_call else R.drawable.ic_call)
            .setContentTitle(callerName)
            .setContentText("Incoming $callType")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setOngoing(true)

        notificationManager.notify(notificationId, builder.build())
    }

    /**
     * Cancel a notification
     */
    fun cancelNotification(context: Context, notificationId: Int) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.cancel(notificationId)
    }

    /**
     * Cancel all notifications
     */
    fun cancelAllNotifications(context: Context) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.cancelAll()
    }

    /**
     * Show a group notification summary
     */
    fun showGroupNotification(
        context: Context,
        conversationId: String,
        groupName: String,
        messages: List<Pair<String, String>>, // Pair of sender name and message
        notificationId: Int = conversationId.hashCode()
    ) {
        val settings = SettingsRepository.getSettings()
        
        if (!settings.notificationsEnabled) return

        val notificationManager = context.getSystemService(NotificationManager::class.java)

        val intent = Intent(context, ChatActivity::class.java).apply {
            putExtra(ChatActivity.EXTRA_CONVERSATION_ID, conversationId)
            putExtra(ChatActivity.EXTRA_CONTACT_NAME, groupName)
            putExtra(ChatActivity.EXTRA_IS_GROUP, true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val inboxStyle = NotificationCompat.InboxStyle()
            .setBigContentTitle(groupName)
        
        messages.takeLast(5).forEach { (sender, message) ->
            inboxStyle.addLine("$sender: $message")
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_MESSAGES)
            .setSmallIcon(R.drawable.ic_message)
            .setContentTitle(groupName)
            .setContentText("${messages.size} new messages")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setStyle(inboxStyle)
            .setNumber(messages.size)

        if (!settings.soundEnabled) {
            builder.setSilent(true)
        }

        if (settings.vibrationEnabled) {
            builder.setVibrate(longArrayOf(0, 250, 250, 250))
        }

        notificationManager.notify(notificationId, builder.build())
    }
}

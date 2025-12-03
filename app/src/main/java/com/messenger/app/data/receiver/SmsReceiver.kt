package com.messenger.app.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

/**
 * Broadcast receiver for incoming SMS messages
 */
class SmsReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            messages?.forEach { message ->
                val sender = message.displayOriginatingAddress
                val body = message.displayMessageBody
                // TODO: Handle incoming SMS - notify UI or save to database
            }
        }
    }
}

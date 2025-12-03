package com.messenger.app

import android.app.Application
import com.messenger.app.service.NotificationService

/**
 * Main application class for the Messenger app
 */
class MessengerApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Initialize notification channels
        NotificationService.createNotificationChannels(this)
    }
    
    companion object {
        lateinit var instance: MessengerApplication
            private set
    }
}

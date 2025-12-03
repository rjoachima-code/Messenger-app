package com.messenger.app

import android.app.Application

/**
 * Main application class for the Messenger app
 */
class MessengerApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    
    companion object {
        lateinit var instance: MessengerApplication
            private set
    }
}

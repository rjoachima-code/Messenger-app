# Messenger App

A comprehensive iMessage-style messaging app for Android that unifies multiple messaging platforms.

## Features

### 📱 iMessage-Inspired Design
- Clean, minimalist interface with rounded message bubbles
- Blue bubbles for sent messages, gray for received
- Smooth gradients and modern styling
- Platform badges on conversation avatars

### 🔗 Multi-Platform Integration
- SMS (default messages)
- Facebook Messenger
- Instagram DMs
- TikTok messages
- Visual indicators for each platform
- Filter conversations by platform

### 💬 Core Functionality
- Conversation list with unread badges
- Real-time online status indicators
- Search functionality
- Individual chat view with message history
- Message input with camera, image, and voice options
- Timestamp display
- Send button that activates when text is entered

### ✓ Message Status Indicators
- ✓ Sent (single check)
- ✓✓ Delivered (double check)
- ✓✓ Read (blue double check)
- Real-time status updates

### 💬 Typing Indicators
- Animated "typing..." display
- Shows in chat header and conversation list
- Real-time typing detection

### 🎙️ Voice Messages
- Hold mic button to record
- Visual recording timer
- Cancel or send recording

### 😊 Reactions & Tapbacks
- Long-press message for reactions
- Quick reaction with double-tap (heart)
- 6 reaction options: ❤️ 👍 😂 😮 😢 😡
- Reaction counts displayed

### 📋 Message Actions
- Long-press menu with:
  - React
  - Copy
  - Reply
  - Delete
- Selection mode for bulk operations

### 📞 Communication Options
- Voice call button
- Video call button
- Contact info button

### 📸 Media Support
- Image messages
- Camera integration
- Photo gallery access

### 🎨 UI Elements
- Rounded message bubbles with tail effect
- Platform-specific color coding
- Online status dots
- Unread message counters
- Smooth transitions between views

## Technical Details

- **Minimum SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 14 (API 34)
- **Language**: Kotlin
- **Architecture**: MVVM with Repository pattern
- **UI**: XML layouts with ViewBinding

## Building

```bash
./gradlew assembleDebug
```

## Dependencies

- AndroidX Core KTX
- AndroidX AppCompat
- Material Design Components
- AndroidX ConstraintLayout
- AndroidX RecyclerView
- AndroidX CardView
- AndroidX Lifecycle Components
- Kotlin Coroutines
- Coil (Image Loading)

## Permissions Required

- READ_SMS / SEND_SMS / RECEIVE_SMS - For SMS messaging
- READ_CONTACTS - For contact access
- INTERNET / ACCESS_NETWORK_STATE - For platform APIs
- CAMERA - For camera integration
- READ_MEDIA_IMAGES / READ_MEDIA_VIDEO - For media access
- RECORD_AUDIO - For voice messages
- CALL_PHONE - For voice calls

## Future Enhancements

To integrate real APIs, you'll need to:
1. Set up authentication with Facebook Graph API, Instagram Basic Display API, and TikTok API
2. Implement WebSocket connections for real-time messaging
3. Add proper data persistence with SQLite or Room
4. Handle media uploads and downloads
5. Implement notification services

## License

This project is for educational purposes.

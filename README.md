# Messenger App

A modern SMS/MMS messaging app replacement for Android with a clean, intuitive design.

## Features

### 📱 Modern Design
- Clean, minimalist interface with rounded message bubbles
- Blue bubbles for sent messages, gray for received
- Smooth gradients and modern styling
- **Dark Mode support** with system theme integration

### 💬 Core Functionality
- Conversation list with unread badges
- Real-time online status indicators
- Search functionality
- Individual chat view with message history
- Message input with camera, image, and voice options
- Timestamp display
- Send button that activates when text is entered
- **New Message Composer** with contact picker

### 👥 Group Chats
- Create group conversations (MMS)
- Add multiple participants
- Group naming
- Admin management
- Group typing indicators ("Sarah and Mike are typing...")

### ✓ Message Status Indicators
- ✓ Sent (single check)
- ✓✓ Delivered (double check)
- ✓✓ Read (blue double check)
- Real-time status updates

### 💬 Typing Indicators
- Animated "typing..." display
- Shows in chat header and conversation list
- Real-time typing detection
- Multi-user typing in groups

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
  - **Forward** to other conversations
  - **Pin/Unpin** messages
  - Delete
- Selection mode for bulk operations

### 📌 Pinned Messages
- Pin important messages for quick access
- Unpin when no longer needed

### ↪️ Message Forwarding
- Forward messages to other conversations
- Forwarded message indicator
- Original sender attribution

### ✏️ Message Editing
- Edit sent messages within 15 minutes
- "Edited" indicator on modified messages
- Original content preserved

### 📞 Communication Options
- Voice call button
- Video call button
- Contact info button

### 📸 Media Support
- Image messages (MMS)
- Camera integration
- Photo gallery access

### 🔔 Notifications
- Push notification support structure
- Customizable notification sounds
- Vibration settings
- Message preview options

### ⚙️ Settings
- **Appearance**: Dark mode, font size
- **Notifications**: Enable/disable, sound, vibration, message preview
- **Privacy**: Read receipts, typing indicators, last seen visibility
- **Chat**: Enter to send option
- **Storage**: Cache and media management

### 🎨 UI Elements
- Rounded message bubbles with tail effect
- SMS/MMS indicators
- Online status dots
- Unread message counters
- Smooth transitions between views
- Mute/Archive conversation support

## Technical Details

- **Minimum SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 14 (API 34)
- **Language**: Kotlin
- **Architecture**: MVVM with Repository pattern
- **UI**: XML layouts with ViewBinding

## Building

### GitHub Actions (Recommended)
The project includes a GitHub Actions workflow that automatically builds the APK on push and pull requests. The built APK artifact can be downloaded from the Actions tab.

### Local Build
```bash
./gradlew assembleDebug
```

**Requirements**:
- JDK 17 or higher
- Android SDK with API 34
- Network access to Google Maven repository

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
- CAMERA - For camera integration
- READ_MEDIA_IMAGES / READ_MEDIA_VIDEO - For media access (MMS)
- RECORD_AUDIO - For voice messages
- CALL_PHONE - For voice calls
- POST_NOTIFICATIONS - For push notifications

## Future Enhancements

1. Implement WebSocket connections for real-time messaging
2. Add proper data persistence with SQLite or Room
3. Handle media uploads and downloads
4. Implement notification services with Firebase Cloud Messaging
5. Add end-to-end encryption for secure messaging
6. RCS messaging support

## License

This project is for educational purposes.

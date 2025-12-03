package com.messenger.app.ui.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.messenger.app.R
import com.messenger.app.data.model.Message
import com.messenger.app.data.repository.MessageRepository

/**
 * Activity for displaying a single chat conversation
 */
class ChatActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CONVERSATION_ID = "conversation_id"
        const val EXTRA_CONTACT_NAME = "contact_name"
        const val EXTRA_IS_ONLINE = "is_online"
    }

    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var voiceButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var contactName: TextView
    private lateinit var statusText: TextView
    private lateinit var onlineIndicator: View
    private lateinit var avatarInitials: TextView
    private lateinit var recordingIndicator: LinearLayout
    private lateinit var recordingTime: TextView
    private lateinit var attachButton: ImageButton
    private lateinit var cameraButton: ImageButton
    private lateinit var emojiButton: ImageButton

    private lateinit var adapter: MessageAdapter
    private var conversationId: String = ""
    private var isRecording = false
    private var recordingStartTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        conversationId = intent.getStringExtra(EXTRA_CONVERSATION_ID) ?: return finish()
        val name = intent.getStringExtra(EXTRA_CONTACT_NAME) ?: "Unknown"
        val isOnline = intent.getBooleanExtra(EXTRA_IS_ONLINE, false)

        initViews()
        setupHeader(name, isOnline)
        setupRecyclerView()
        setupMessageInput()
        setupVoiceRecording()
        setupActionButtons()
        loadMessages()
    }

    private fun initViews() {
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        voiceButton = findViewById(R.id.voiceButton)
        backButton = findViewById(R.id.backButton)
        contactName = findViewById(R.id.contactName)
        statusText = findViewById(R.id.statusText)
        onlineIndicator = findViewById(R.id.onlineIndicator)
        avatarInitials = findViewById(R.id.avatarInitials)
        recordingIndicator = findViewById(R.id.recordingIndicator)
        recordingTime = findViewById(R.id.recordingTime)
        attachButton = findViewById(R.id.attachButton)
        cameraButton = findViewById(R.id.cameraButton)
        emojiButton = findViewById(R.id.emojiButton)
    }

    private fun setupHeader(name: String, isOnline: Boolean) {
        contactName.text = name
        avatarInitials.text = getInitials(name)
        
        if (isOnline) {
            statusText.text = getString(R.string.online)
            statusText.setTextColor(getColor(R.color.online_status))
            onlineIndicator.visibility = View.VISIBLE
        } else {
            statusText.text = getString(R.string.offline)
            statusText.setTextColor(getColor(R.color.text_secondary))
            onlineIndicator.visibility = View.GONE
        }

        backButton.setOnClickListener { finish() }
        
        // Call buttons
        findViewById<ImageButton>(R.id.videoCallButton).setOnClickListener {
            Toast.makeText(this, getString(R.string.video_call), Toast.LENGTH_SHORT).show()
        }
        findViewById<ImageButton>(R.id.voiceCallButton).setOnClickListener {
            Toast.makeText(this, getString(R.string.voice_call), Toast.LENGTH_SHORT).show()
        }
        findViewById<ImageButton>(R.id.infoButton).setOnClickListener {
            Toast.makeText(this, getString(R.string.contact_info), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        adapter = MessageAdapter(
            onMessageLongClick = { message, view -> showMessageActions(message, view) },
            onMessageDoubleClick = { message -> quickReaction(message) }
        )
        
        val layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        messagesRecyclerView.layoutManager = layoutManager
        messagesRecyclerView.adapter = adapter
    }

    private fun setupMessageInput() {
        messageInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val hasText = !s.isNullOrEmpty()
                sendButton.visibility = if (hasText) View.VISIBLE else View.GONE
                voiceButton.visibility = if (hasText) View.GONE else View.VISIBLE
            }
        })

        sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun setupVoiceRecording() {
        voiceButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startRecording()
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    stopRecording(event.action == MotionEvent.ACTION_UP)
                    true
                }
                else -> false
            }
        }

        findViewById<ImageButton>(R.id.cancelRecording).setOnClickListener {
            stopRecording(false)
        }
    }

    private fun setupActionButtons() {
        attachButton.setOnClickListener {
            // TODO: Show attachment options (photos, files, location, etc.)
            Toast.makeText(this, "Attachments", Toast.LENGTH_SHORT).show()
        }

        cameraButton.setOnClickListener {
            // TODO: Open camera
            Toast.makeText(this, getString(R.string.camera), Toast.LENGTH_SHORT).show()
        }

        emojiButton.setOnClickListener {
            // TODO: Show emoji picker
            Toast.makeText(this, "Emoji picker", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadMessages() {
        val messages = MessageRepository.getMessages(conversationId)
        adapter.submitList(messages) {
            // Scroll to bottom after loading
            if (messages.isNotEmpty()) {
                messagesRecyclerView.scrollToPosition(messages.size - 1)
            }
        }
        
        // Mark conversation as read
        MessageRepository.markConversationAsRead(conversationId)
    }

    private fun sendMessage() {
        val text = messageInput.text?.toString()?.trim() ?: return
        if (text.isEmpty()) return

        // Send message
        val message = MessageRepository.sendMessage(conversationId, text)
        
        // Clear input
        messageInput.text?.clear()

        // Reload messages
        loadMessages()
    }

    private fun startRecording() {
        isRecording = true
        recordingStartTime = System.currentTimeMillis()
        recordingIndicator.visibility = View.VISIBLE
        updateRecordingTime()
    }

    private fun stopRecording(shouldSend: Boolean) {
        if (!isRecording) return
        
        isRecording = false
        recordingIndicator.visibility = View.GONE
        
        if (shouldSend) {
            val duration = ((System.currentTimeMillis() - recordingStartTime) / 1000).toInt()
            if (duration >= 1) {
                // TODO: Send voice message
                Toast.makeText(this, "Voice message: ${duration}s", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateRecordingTime() {
        if (!isRecording) return
        
        val elapsed = (System.currentTimeMillis() - recordingStartTime) / 1000
        val minutes = elapsed / 60
        val seconds = elapsed % 60
        recordingTime.text = String.format("%d:%02d", minutes, seconds)
        
        recordingTime.postDelayed({ updateRecordingTime() }, 1000)
    }

    private fun showMessageActions(message: Message, anchorView: View) {
        val popupView = LayoutInflater.from(this).inflate(R.layout.popup_message_actions, null)
        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popupView.findViewById<View>(R.id.actionReact).setOnClickListener {
            popupWindow.dismiss()
            showReactionPicker(message, anchorView)
        }

        popupView.findViewById<View>(R.id.actionCopy).setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("message", message.content))
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        popupView.findViewById<View>(R.id.actionReply).setOnClickListener {
            // TODO: Implement reply
            Toast.makeText(this, "Reply to: ${message.content}", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        popupView.findViewById<View>(R.id.actionDelete).setOnClickListener {
            MessageRepository.deleteMessage(message.id, conversationId)
            loadMessages()
            popupWindow.dismiss()
        }

        popupWindow.elevation = 8f
        popupWindow.showAsDropDown(anchorView, 0, -anchorView.height, Gravity.CENTER)
    }

    private fun showReactionPicker(message: Message, anchorView: View) {
        val popupView = LayoutInflater.from(this).inflate(R.layout.popup_reactions, null)
        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        val reactions = listOf(
            R.id.reaction1 to "❤️",
            R.id.reaction2 to "👍",
            R.id.reaction3 to "😂",
            R.id.reaction4 to "😮",
            R.id.reaction5 to "😢",
            R.id.reaction6 to "😡"
        )

        reactions.forEach { (viewId, emoji) ->
            popupView.findViewById<TextView>(viewId).setOnClickListener {
                MessageRepository.addReaction(message.id, conversationId, emoji)
                loadMessages()
                popupWindow.dismiss()
            }
        }

        popupWindow.elevation = 8f
        popupWindow.showAsDropDown(anchorView, 0, -anchorView.height - 100, Gravity.CENTER)
    }

    private fun quickReaction(message: Message) {
        // Double tap adds heart reaction
        MessageRepository.addReaction(message.id, conversationId, "❤️")
        loadMessages()
    }

    private fun getInitials(name: String): String {
        val parts = name.split(" ")
        return when {
            parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}"
            parts.isNotEmpty() -> parts[0].take(2)
            else -> "?"
        }.uppercase()
    }
}

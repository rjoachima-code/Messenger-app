package com.messenger.app.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.messenger.app.R
import com.messenger.app.data.model.Conversation
import com.messenger.app.data.model.Platform
import com.messenger.app.data.repository.MessageRepository
import com.messenger.app.data.repository.SettingsRepository
import com.messenger.app.ui.chat.ChatActivity
import com.messenger.app.ui.compose.NewMessageActivity
import com.messenger.app.ui.conversations.ConversationAdapter
import com.messenger.app.ui.settings.SettingsActivity

/**
 * Main activity displaying the list of conversations
 */
class MainActivity : AppCompatActivity() {

    private lateinit var conversationsRecyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var platformChipGroup: ChipGroup
    private lateinit var emptyState: LinearLayout
    private lateinit var fabNewMessage: FloatingActionButton
    private lateinit var composeButton: ImageButton
    private lateinit var settingsButton: ImageButton

    private lateinit var adapter: ConversationAdapter
    private var currentPlatformFilter: Platform? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize settings
        SettingsRepository.init(this)
        
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        setupSearch()
        setupPlatformFilter()
        setupButtons()
        loadConversations()
    }

    private fun initViews() {
        conversationsRecyclerView = findViewById(R.id.conversationsRecyclerView)
        searchEditText = findViewById(R.id.searchEditText)
        platformChipGroup = findViewById(R.id.platformChipGroup)
        emptyState = findViewById(R.id.emptyState)
        fabNewMessage = findViewById(R.id.fabNewMessage)
        composeButton = findViewById(R.id.composeButton)
        settingsButton = findViewById(R.id.settingsButton)
    }

    private fun setupRecyclerView() {
        adapter = ConversationAdapter { conversation ->
            openChat(conversation)
        }
        conversationsRecyclerView.layoutManager = LinearLayoutManager(this)
        conversationsRecyclerView.adapter = adapter
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterConversations(s?.toString() ?: "")
            }
        })
    }

    private fun setupPlatformFilter() {
        platformChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            currentPlatformFilter = when {
                checkedIds.contains(R.id.chipSms) -> Platform.SMS
                checkedIds.contains(R.id.chipFacebook) -> Platform.FACEBOOK
                checkedIds.contains(R.id.chipInstagram) -> Platform.INSTAGRAM
                checkedIds.contains(R.id.chipTiktok) -> Platform.TIKTOK
                else -> null // "All" or no selection
            }
            filterConversations(searchEditText.text?.toString() ?: "")
        }
    }

    private fun setupButtons() {
        fabNewMessage.setOnClickListener {
            openNewMessage()
        }
        composeButton.setOnClickListener {
            openNewMessage()
        }
        settingsButton.setOnClickListener {
            openSettings()
        }
    }

    private fun loadConversations() {
        val conversations = MessageRepository.getConversations()
        updateConversationsList(conversations)
    }

    private fun filterConversations(query: String) {
        val conversations = if (query.isEmpty()) {
            if (currentPlatformFilter != null) {
                MessageRepository.getConversationsByPlatform(currentPlatformFilter!!)
            } else {
                MessageRepository.getConversations()
            }
        } else {
            MessageRepository.searchConversations(query).let { results ->
                if (currentPlatformFilter != null) {
                    results.filter { it.platform == currentPlatformFilter }
                } else {
                    results
                }
            }
        }
        updateConversationsList(conversations)
    }

    private fun updateConversationsList(conversations: List<Conversation>) {
        if (conversations.isEmpty()) {
            conversationsRecyclerView.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        } else {
            conversationsRecyclerView.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
            adapter.submitList(conversations)
        }
    }

    private fun openChat(conversation: Conversation) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra(ChatActivity.EXTRA_CONVERSATION_ID, conversation.id)
            putExtra(ChatActivity.EXTRA_CONTACT_NAME, conversation.getDisplayName())
            putExtra(ChatActivity.EXTRA_IS_ONLINE, conversation.contact.isOnline)
            putExtra(ChatActivity.EXTRA_IS_GROUP, conversation.isGroup)
        }
        startActivity(intent)
    }
    
    private fun openNewMessage() {
        val intent = Intent(this, NewMessageActivity::class.java)
        startActivity(intent)
    }
    
    private fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        // Refresh conversations when returning to the screen
        loadConversations()
    }
}

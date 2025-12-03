package com.messenger.app.ui.compose

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.messenger.app.R
import com.messenger.app.data.model.Contact
import com.messenger.app.data.repository.MessageRepository
import com.messenger.app.ui.chat.ChatActivity

/**
 * Activity for composing a new message
 * Allows selecting a contact or entering a phone number
 */
class NewMessageActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var searchEditText: EditText
    private lateinit var contactsRecyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var createGroupButton: LinearLayout
    
    private lateinit var adapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        initViews()
        setupRecyclerView()
        setupSearch()
        loadContacts()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        searchEditText = findViewById(R.id.searchEditText)
        contactsRecyclerView = findViewById(R.id.contactsRecyclerView)
        emptyState = findViewById(R.id.emptyState)
        createGroupButton = findViewById(R.id.createGroupButton)

        backButton.setOnClickListener { finish() }
        
        createGroupButton.setOnClickListener {
            // TODO: Open create group screen
            val intent = Intent(this, CreateGroupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        adapter = ContactAdapter { contact ->
            openChatWithContact(contact)
        }
        contactsRecyclerView.layoutManager = LinearLayoutManager(this)
        contactsRecyclerView.adapter = adapter
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterContacts(s?.toString() ?: "")
            }
        })
    }

    private fun loadContacts() {
        val contacts = MessageRepository.getContacts()
        updateContactsList(contacts)
    }

    private fun filterContacts(query: String) {
        val contacts = if (query.isEmpty()) {
            MessageRepository.getContacts()
        } else {
            MessageRepository.searchContacts(query)
        }
        updateContactsList(contacts)
    }

    private fun updateContactsList(contacts: List<Contact>) {
        if (contacts.isEmpty()) {
            contactsRecyclerView.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        } else {
            contactsRecyclerView.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
            adapter.submitList(contacts)
        }
    }

    private fun openChatWithContact(contact: Contact) {
        // Create or get existing conversation
        val conversation = MessageRepository.createConversation(contact)
        
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra(ChatActivity.EXTRA_CONVERSATION_ID, conversation.id)
            putExtra(ChatActivity.EXTRA_CONTACT_NAME, contact.name)
            putExtra(ChatActivity.EXTRA_IS_ONLINE, contact.isOnline)
        }
        startActivity(intent)
        finish()
    }
}

package com.messenger.app.ui.compose

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.messenger.app.R
import com.messenger.app.data.model.Contact
import com.messenger.app.data.repository.MessageRepository
import com.messenger.app.ui.chat.ChatActivity

/**
 * Activity for creating a new group chat
 */
class CreateGroupActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var nextButton: TextView
    private lateinit var groupNameEditText: EditText
    private lateinit var searchEditText: EditText
    private lateinit var selectedChipGroup: ChipGroup
    private lateinit var contactsRecyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var selectedCountText: TextView

    private lateinit var adapter: SelectableContactAdapter
    private val selectedContacts = mutableListOf<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        initViews()
        setupRecyclerView()
        setupSearch()
        loadContacts()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        nextButton = findViewById(R.id.nextButton)
        groupNameEditText = findViewById(R.id.groupNameEditText)
        searchEditText = findViewById(R.id.searchEditText)
        selectedChipGroup = findViewById(R.id.selectedChipGroup)
        contactsRecyclerView = findViewById(R.id.contactsRecyclerView)
        emptyState = findViewById(R.id.emptyState)
        selectedCountText = findViewById(R.id.selectedCountText)

        backButton.setOnClickListener { finish() }
        
        nextButton.setOnClickListener {
            createGroup()
        }

        updateNextButton()
    }

    private fun setupRecyclerView() {
        adapter = SelectableContactAdapter(
            onContactSelected = { contact, isSelected ->
                if (isSelected) {
                    selectedContacts.add(contact)
                    addChip(contact)
                } else {
                    selectedContacts.remove(contact)
                    removeChip(contact)
                }
                updateNextButton()
                updateSelectedCount()
            }
        )
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

    private fun addChip(contact: Contact) {
        val chip = Chip(this).apply {
            text = contact.name
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                selectedContacts.remove(contact)
                selectedChipGroup.removeView(this)
                adapter.deselectContact(contact)
                updateNextButton()
                updateSelectedCount()
            }
        }
        selectedChipGroup.addView(chip)
        selectedChipGroup.visibility = View.VISIBLE
    }

    private fun removeChip(contact: Contact) {
        for (i in 0 until selectedChipGroup.childCount) {
            val chip = selectedChipGroup.getChildAt(i) as? Chip
            if (chip?.text == contact.name) {
                selectedChipGroup.removeViewAt(i)
                break
            }
        }
        if (selectedChipGroup.childCount == 0) {
            selectedChipGroup.visibility = View.GONE
        }
    }

    private fun updateNextButton() {
        val hasName = groupNameEditText.text.isNotEmpty()
        val hasContacts = selectedContacts.size >= 2
        nextButton.isEnabled = hasContacts
        nextButton.alpha = if (hasContacts) 1.0f else 0.5f
    }

    private fun updateSelectedCount() {
        selectedCountText.text = getString(R.string.selected_count, selectedContacts.size)
        selectedCountText.visibility = if (selectedContacts.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun createGroup() {
        if (selectedContacts.size < 2) {
            Toast.makeText(this, getString(R.string.group_min_members), Toast.LENGTH_SHORT).show()
            return
        }

        val groupName = groupNameEditText.text.toString().ifEmpty {
            selectedContacts.take(3).joinToString(", ") { it.name }
        }

        val conversation = MessageRepository.createGroupConversation(groupName, selectedContacts)
        
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra(ChatActivity.EXTRA_CONVERSATION_ID, conversation.id)
            putExtra(ChatActivity.EXTRA_CONTACT_NAME, groupName)
            putExtra(ChatActivity.EXTRA_IS_ONLINE, false)
        }
        startActivity(intent)
        finish()
    }
}

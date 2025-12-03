package com.messenger.app.ui.compose

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.messenger.app.R
import com.messenger.app.data.model.Contact
import com.messenger.app.data.model.Platform

/**
 * Adapter for displaying contacts with selection checkboxes
 */
class SelectableContactAdapter(
    private val onContactSelected: (Contact, Boolean) -> Unit
) : ListAdapter<Contact, SelectableContactAdapter.ViewHolder>(ContactDiffCallback()) {

    private val selectedContactIds = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact_selectable, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun deselectContact(contact: Contact) {
        selectedContactIds.remove(contact.id)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatarInitials: TextView = itemView.findViewById(R.id.avatarInitials)
        private val platformBadge: View = itemView.findViewById(R.id.platformBadge)
        private val onlineIndicator: View = itemView.findViewById(R.id.onlineIndicator)
        private val contactName: TextView = itemView.findViewById(R.id.contactName)
        private val contactInfo: TextView = itemView.findViewById(R.id.contactInfo)
        private val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)

        fun bind(contact: Contact) {
            avatarInitials.text = getInitials(contact.name)
            
            // Set platform badge
            platformBadge.setBackgroundResource(getPlatformBadgeDrawable(contact.platform))
            
            // Show online indicator
            onlineIndicator.visibility = if (contact.isOnline) View.VISIBLE else View.GONE
            
            contactName.text = contact.name
            contactInfo.text = contact.phoneNumber ?: contact.email ?: contact.platform.displayName
            
            checkbox.isChecked = selectedContactIds.contains(contact.id)
            
            val clickListener = View.OnClickListener {
                val isSelected = !selectedContactIds.contains(contact.id)
                if (isSelected) {
                    selectedContactIds.add(contact.id)
                } else {
                    selectedContactIds.remove(contact.id)
                }
                checkbox.isChecked = isSelected
                onContactSelected(contact, isSelected)
            }
            
            itemView.setOnClickListener(clickListener)
            checkbox.setOnClickListener(clickListener)
        }

        private fun getInitials(name: String): String {
            val parts = name.split(" ")
            return when {
                parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}"
                parts.isNotEmpty() -> parts[0].take(2)
                else -> "?"
            }.uppercase()
        }

        private fun getPlatformBadgeDrawable(platform: Platform): Int {
            return when (platform) {
                Platform.SMS -> R.drawable.platform_badge_sms
                Platform.FACEBOOK -> R.drawable.platform_badge_facebook
                Platform.INSTAGRAM -> R.drawable.platform_badge_instagram
                Platform.TIKTOK -> R.drawable.platform_badge_tiktok
                Platform.IMESSAGE -> R.drawable.platform_badge_imessage
            }
        }
    }

    class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }
    }
}

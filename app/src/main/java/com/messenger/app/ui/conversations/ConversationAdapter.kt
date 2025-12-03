package com.messenger.app.ui.conversations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.messenger.app.R
import com.messenger.app.data.model.Conversation
import com.messenger.app.data.model.Platform

/**
 * Adapter for displaying the list of conversations
 */
class ConversationAdapter(
    private val onConversationClick: (Conversation) -> Unit
) : ListAdapter<Conversation, ConversationAdapter.ViewHolder>(ConversationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_conversation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatarInitials: TextView = itemView.findViewById(R.id.avatarInitials)
        private val platformBadge: View = itemView.findViewById(R.id.platformBadge)
        private val onlineIndicator: View = itemView.findViewById(R.id.onlineIndicator)
        private val contactName: TextView = itemView.findViewById(R.id.contactName)
        private val timeText: TextView = itemView.findViewById(R.id.timeText)
        private val lastMessage: TextView = itemView.findViewById(R.id.lastMessage)
        private val unreadBadge: TextView = itemView.findViewById(R.id.unreadBadge)
        private val typingIndicator: TextView = itemView.findViewById(R.id.typingIndicator)

        fun bind(conversation: Conversation) {
            // Set avatar initials (use group initials for groups)
            avatarInitials.text = conversation.getAvatarInitials()
            
            // Set platform badge background
            platformBadge.setBackgroundResource(getPlatformBadgeDrawable(conversation.platform))
            
            // Show/hide online indicator (hide for groups)
            onlineIndicator.visibility = if (!conversation.isGroup && conversation.contact.isOnline) View.VISIBLE else View.GONE
            
            // Set contact/group name
            contactName.text = conversation.getDisplayName()
            
            // Set time
            timeText.text = conversation.getFormattedTime()
            
            // Set last message or typing indicator
            val typingText = conversation.getTypingText()
            if (typingText.isNotEmpty()) {
                lastMessage.visibility = View.GONE
                typingIndicator.visibility = View.VISIBLE
                typingIndicator.text = typingText
            } else {
                lastMessage.visibility = View.VISIBLE
                lastMessage.text = conversation.lastMessage
                typingIndicator.visibility = View.GONE
            }
            
            // Set unread badge
            if (conversation.unreadCount > 0) {
                unreadBadge.visibility = View.VISIBLE
                unreadBadge.text = if (conversation.unreadCount > 99) "99+" else conversation.unreadCount.toString()
            } else {
                unreadBadge.visibility = View.GONE
            }
            
            // Click listener
            itemView.setOnClickListener { onConversationClick(conversation) }
        }
        
        private fun getPlatformBadgeDrawable(platform: Platform): Int {
            return when (platform) {
                Platform.SMS -> R.drawable.platform_badge_sms
                Platform.MMS -> R.drawable.platform_badge_mms
            }
        }
    }

    class ConversationDiffCallback : DiffUtil.ItemCallback<Conversation>() {
        override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem == newItem
        }
    }
}

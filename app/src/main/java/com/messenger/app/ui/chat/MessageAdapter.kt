package com.messenger.app.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.messenger.app.R
import com.messenger.app.data.model.Message
import com.messenger.app.data.model.MessageStatus
import com.messenger.app.data.model.MessageType
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying messages in a chat
 */
class MessageAdapter(
    private val onMessageLongClick: (Message, View) -> Unit,
    private val onMessageDoubleClick: (Message) -> Unit
) : ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_OUTGOING = 0
        private const val VIEW_TYPE_INCOMING = 1
    }

    private var lastClickTime = 0L
    private var lastClickedMessage: Message? = null

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isOutgoing) VIEW_TYPE_OUTGOING else VIEW_TYPE_INCOMING
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutId = if (viewType == VIEW_TYPE_OUTGOING) {
            R.layout.item_message_outgoing
        } else {
            R.layout.item_message_incoming
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return if (viewType == VIEW_TYPE_OUTGOING) {
            OutgoingMessageViewHolder(view)
        } else {
            IncomingMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is OutgoingMessageViewHolder -> holder.bind(message)
            is IncomingMessageViewHolder -> holder.bind(message)
        }
    }

    inner class OutgoingMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageContent: TextView = itemView.findViewById(R.id.messageContent)
        private val timeText: TextView = itemView.findViewById(R.id.timeText)
        private val statusIcon: ImageView = itemView.findViewById(R.id.statusIcon)
        private val reactionsContainer: LinearLayout = itemView.findViewById(R.id.reactionsContainer)
        private val reactionEmojis: TextView = itemView.findViewById(R.id.reactionEmojis)
        private val imageContent: ImageView = itemView.findViewById(R.id.imageContent)
        private val voiceContent: LinearLayout = itemView.findViewById(R.id.voiceContent)

        fun bind(message: Message) {
            // Show/hide content based on message type
            when (message.type) {
                MessageType.TEXT -> {
                    messageContent.visibility = View.VISIBLE
                    messageContent.text = message.content
                    imageContent.visibility = View.GONE
                    voiceContent.visibility = View.GONE
                }
                MessageType.IMAGE -> {
                    messageContent.visibility = View.GONE
                    imageContent.visibility = View.VISIBLE
                    voiceContent.visibility = View.GONE
                    // TODO: Load image with Coil
                }
                MessageType.VOICE -> {
                    messageContent.visibility = View.GONE
                    imageContent.visibility = View.GONE
                    voiceContent.visibility = View.VISIBLE
                }
                else -> {
                    messageContent.visibility = View.VISIBLE
                    messageContent.text = message.content
                    imageContent.visibility = View.GONE
                    voiceContent.visibility = View.GONE
                }
            }

            // Set time
            timeText.text = formatTime(message.timestamp)

            // Set status icon
            statusIcon.visibility = View.VISIBLE
            when (message.status) {
                MessageStatus.SENDING -> {
                    statusIcon.setImageResource(R.drawable.ic_check)
                    statusIcon.alpha = 0.5f
                }
                MessageStatus.SENT -> {
                    statusIcon.setImageResource(R.drawable.ic_check)
                    statusIcon.alpha = 1f
                }
                MessageStatus.DELIVERED -> {
                    statusIcon.setImageResource(R.drawable.ic_double_check)
                    statusIcon.setColorFilter(itemView.context.getColor(android.R.color.white))
                }
                MessageStatus.READ -> {
                    statusIcon.setImageResource(R.drawable.ic_double_check)
                    statusIcon.setColorFilter(itemView.context.getColor(R.color.status_read))
                }
                MessageStatus.FAILED -> {
                    statusIcon.setImageResource(R.drawable.ic_info)
                    statusIcon.setColorFilter(itemView.context.getColor(R.color.recording_red))
                }
            }

            // Show reactions
            if (message.reactions.isNotEmpty()) {
                reactionsContainer.visibility = View.VISIBLE
                val grouped = message.getGroupedReactions()
                reactionEmojis.text = grouped.entries.joinToString(" ") { 
                    "${it.key} ${if (it.value.size > 1) it.value.size else ""}"
                }
            } else {
                reactionsContainer.visibility = View.GONE
            }

            // Long click for actions
            itemView.setOnLongClickListener {
                onMessageLongClick(message, itemView)
                true
            }

            // Double click for quick reaction
            itemView.setOnClickListener {
                val currentTime = System.currentTimeMillis()
                if (lastClickedMessage?.id == message.id && currentTime - lastClickTime < 300) {
                    onMessageDoubleClick(message)
                    lastClickTime = 0
                } else {
                    lastClickTime = currentTime
                    lastClickedMessage = message
                }
            }
        }
    }

    inner class IncomingMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageContent: TextView = itemView.findViewById(R.id.messageContent)
        private val timeText: TextView = itemView.findViewById(R.id.timeText)
        private val reactionsContainer: LinearLayout = itemView.findViewById(R.id.reactionsContainer)
        private val reactionEmojis: TextView = itemView.findViewById(R.id.reactionEmojis)
        private val imageContent: ImageView = itemView.findViewById(R.id.imageContent)
        private val voiceContent: LinearLayout = itemView.findViewById(R.id.voiceContent)

        fun bind(message: Message) {
            // Show/hide content based on message type
            when (message.type) {
                MessageType.TEXT -> {
                    messageContent.visibility = View.VISIBLE
                    messageContent.text = message.content
                    imageContent.visibility = View.GONE
                    voiceContent.visibility = View.GONE
                }
                MessageType.IMAGE -> {
                    messageContent.visibility = View.GONE
                    imageContent.visibility = View.VISIBLE
                    voiceContent.visibility = View.GONE
                }
                MessageType.VOICE -> {
                    messageContent.visibility = View.GONE
                    imageContent.visibility = View.GONE
                    voiceContent.visibility = View.VISIBLE
                }
                else -> {
                    messageContent.visibility = View.VISIBLE
                    messageContent.text = message.content
                    imageContent.visibility = View.GONE
                    voiceContent.visibility = View.GONE
                }
            }

            // Set time
            timeText.text = formatTime(message.timestamp)

            // Show reactions
            if (message.reactions.isNotEmpty()) {
                reactionsContainer.visibility = View.VISIBLE
                val grouped = message.getGroupedReactions()
                reactionEmojis.text = grouped.entries.joinToString(" ") { 
                    "${it.key} ${if (it.value.size > 1) it.value.size else ""}"
                }
            } else {
                reactionsContainer.visibility = View.GONE
            }

            // Long click for actions
            itemView.setOnLongClickListener {
                onMessageLongClick(message, itemView)
                true
            }

            // Double click for quick reaction
            itemView.setOnClickListener {
                val currentTime = System.currentTimeMillis()
                if (lastClickedMessage?.id == message.id && currentTime - lastClickTime < 300) {
                    onMessageDoubleClick(message)
                    lastClickTime = 0
                } else {
                    lastClickTime = currentTime
                    lastClickedMessage = message
                }
            }
        }
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}

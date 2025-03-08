package com.idz.ChallengeZone.adapter.chatAdapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.idz.ChallengeZone.R
import com.idz.ChallengeZone.model.ChatMessage
import com.idz.ChallengeZone.databinding.ItemChatMessageBinding

class ChatViewHolder(private val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(message: ChatMessage) {
        binding.textMessage.text = message.text
        binding.textMessage.setBackgroundResource(
            if (message.isUser) R.drawable.user_message_background else R.drawable.model_message_background
        )
    }
}
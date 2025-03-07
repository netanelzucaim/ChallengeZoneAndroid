package com.idz.ChallengeZone.adapter.chatAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.idz.ChallengeZone.databinding.ItemChatMessageBinding
import com.idz.ChallengeZone.model.ChatMessage

class ChatAdapter(private val messages: List<ChatMessage>) : BaseAdapter() {

    override fun getCount(): Int = messages.size

    override fun getItem(position: Int): Any = messages[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(parent?.context)
        val binding = convertView?.let {
            ItemChatMessageBinding.bind(it)
        } ?: ItemChatMessageBinding.inflate(inflater, parent, false)
        val holder = ChatViewHolder(binding)
        holder.bind(messages[position])
        return binding.root
    }
}
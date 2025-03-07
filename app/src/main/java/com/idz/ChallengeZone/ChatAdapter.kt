//package com.idz.ChallengeZone
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.idz.ChallengeZone.model.ChatMessage
//
//class ChatAdapter(private val messages: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
//
//    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val textMessage: TextView = view.findViewById(R.id.text_message)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
//        return ChatViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
//        val message = messages[position]
//        holder.textMessage.text = message.text
//        // Set background based on whether the message is from the user or the model
//        holder.textMessage.setBackgroundResource(
//            if (message.isUser) R.drawable.user_message_background else R.drawable.model_message_background
//        )
//    }
//
//    override fun getItemCount() = messages.size
//}
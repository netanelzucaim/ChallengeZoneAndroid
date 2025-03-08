package com.idz.ChallengeZone

import com.idz.ChallengeZone.viewmodel.AskAiViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.idz.ChallengeZone.databinding.FragmentAskAiBinding
import com.idz.ChallengeZone.model.ChatMessage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import com.idz.ChallengeZone.adapter.chatAdapter.ChatRecyclerAdapter

class AskAiFragment : Fragment() {

    private var binding: FragmentAskAiBinding? = null
    private lateinit var chatAdapter: ChatRecyclerAdapter
    private val viewModel: AskAiViewModel by viewModels()
    private lateinit var chat: Chat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAskAiBinding.inflate(inflater, container, false)
        val view = binding?.root

        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-pro-latest",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
        chat = generativeModel.startChat(
            history = listOf(
                content(role = "user") { text("Hello, I have 2 dogs in my house.") },
                content(role = "model") { text("Great to meet you. What would you like to know?") }
            )
        )

        chatAdapter = ChatRecyclerAdapter(viewModel.messages.value ?: mutableListOf())
        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }

        viewModel.messages.observe(viewLifecycleOwner) {
            chatAdapter.notifyDataSetChanged()
        }

        binding?.button?.setOnClickListener {
            sendMessage()
        }

        return view
    }

    private fun sendMessage() {
        val userMessage = binding?.editTextInput?.text.toString()
        if (userMessage.isNotBlank()) {
            val chatMessage = ChatMessage(userMessage, true)
            viewModel.addMessage(chatMessage)
            chatAdapter.notifyItemInserted(viewModel.messages.value?.size?.minus(1) ?: 0)
            binding?.recyclerView?.scrollToPosition(viewModel.messages.value?.size?.minus(1) ?: 0)
            binding?.editTextInput?.text?.clear()

            binding?.button?.isEnabled = false // Disable the button
            MainScope().launch {
                try {
                    val result = chat.sendMessage(userMessage)
                    val responseMessage = ChatMessage(result.text ?: "No response", false)
                    viewModel.addMessage(responseMessage)
                    chatAdapter.notifyItemInserted(viewModel.messages.value?.size?.minus(1) ?: 0)
                    binding?.recyclerView?.scrollToPosition(viewModel.messages.value?.size?.minus(1) ?: 0)
                } finally {
                    binding?.button?.isEnabled = true // Re-enable the button
                }
            }
        }
    }

    public fun buttonSendChat(view: View) {
        sendMessage()
    }
}
package com.idz.ChallengeZone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.idz.ChallengeZone.databinding.FragmentAskAiBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AskAiFragment : Fragment() {

    private var binding: FragmentAskAiBinding? = null
    lateinit var editTextInput: EditText
    lateinit var editTextOutput: EditText
    lateinit var chat: Chat
    var stringBuilder: StringBuilder = java.lang.StringBuilder()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAskAiBinding.inflate(inflater, container, false)
        val view = inflater.inflate(R.layout.fragment_ask_ai, container, false)
        editTextInput = binding?.editTextInput!!
        editTextOutput = binding?.editTextOutput!!
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-pro-latest",
            apiKey = "AIzaSyCdqlIZetWBvJ_wsJ_1Q8CIqBX20ml5bgk"
        )
        chat = generativeModel.startChat(
            history = listOf(
                content(role = "user") { text("Hello, I have 2 dogs in my house.") },
                content(role = "model") { text("Great to meet you. What would you like to know?") }
            )
        )
        stringBuilder.append("Hello, I have 2 dogs in my house.\n\n")
        stringBuilder.append("Great to meet you. What would you like to know?\n\n")

        editTextOutput.setText(stringBuilder.toString())
        binding?.button?.setOnClickListener {
            sendMessage()
        }
        return binding?.root
    }

    private fun sendMessage() {
        stringBuilder.append(editTextInput.text.toString() + "\n\n")
        binding?.button?.isEnabled = false // Disable the button
        MainScope().launch {
            try {
                val result = chat.sendMessage(editTextInput.text.toString())
                stringBuilder.append(result.text + "\n\n")
                editTextOutput.setText(stringBuilder.toString())
                editTextInput.setText("")
            } finally {
                binding?.button?.isEnabled = true // Re-enable the button
            }
        }
    }

    public fun buttonSendChat(view: View) {
        stringBuilder.append(editTextInput.text.toString() + "\n\n")
        binding?.button?.isEnabled = false // Disable the button
        MainScope().launch {
            try {
                val result = chat.sendMessage(editTextInput.text.toString())
                stringBuilder.append(result.text + "\n\n")
                editTextOutput.setText(stringBuilder.toString())
                editTextInput.setText("")
            } finally {
                binding?.button?.isEnabled = true // Re-enable the button
            }
        }
    }
}
package com.idz.ChallengeZone.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.idz.ChallengeZone.model.ChatMessage

class AskAiViewModel : ViewModel() {
    private val _messages = MutableLiveData<MutableList<ChatMessage>>(mutableListOf())
    val messages: LiveData<MutableList<ChatMessage>> get() = _messages

    fun addMessage(message: ChatMessage) {
        _messages.value?.add(message)
        _messages.value = _messages.value // Trigger LiveData update
    }
}
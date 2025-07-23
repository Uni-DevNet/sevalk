package com.sevalk.presentation.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sevalk.data.repositories.ChatMessageItem
import com.sevalk.data.repositories.ChatRepository
import com.sevalk.data.repositories.UserOnlineStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessageItem>>(emptyList())
    val messages: StateFlow<List<ChatMessageItem>> = _messages.asStateFlow()

    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private var currentChatId: String = ""
    private var currentParticipantId: String = ""

    fun initializeChat(chatId: String, participantId: String) {
        if (currentChatId == chatId && currentParticipantId == participantId) {
            return // Already initialized
        }
        
        currentChatId = chatId
        currentParticipantId = participantId
        
        loadMessages()
        observeParticipantOnlineStatus()
        markAllMessagesAsRead()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            chatRepository.getChatMessages(currentChatId)
                .onStart { _isLoading.value = true }
                .catch { e ->
                    Timber.e(e, "Failed to load chat messages")
                    _isLoading.value = false
                }
                .collect { messages ->
                    _isLoading.value = false
                    _messages.value = messages
                }
        }
    }

    private fun observeParticipantOnlineStatus() {
        viewModelScope.launch {
            chatRepository.getUserOnlineStatus(currentParticipantId)
                .catch { e ->
                    Timber.e(e, "Failed to observe participant online status")
                    _isOnline.value = false
                }
                .collect { status ->
                    _isOnline.value = status.isOnline
                }
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            chatRepository.sendMessage(currentChatId, currentParticipantId, text)
                .onFailure { e ->
                    Timber.e(e, "Failed to send message")
                    // TODO: Show error message to user
                }
        }
    }

    private fun markAllMessagesAsRead() {
        viewModelScope.launch {
            chatRepository.markAllMessagesAsRead(currentChatId)
                .onFailure { e ->
                    Timber.e(e, "Failed to mark messages as read")
                }
        }
    }

    fun setUserOnline() {
        viewModelScope.launch {
            chatRepository.setUserOnlineStatus(true)
                .onFailure { e ->
                    Timber.e(e, "Failed to set user online")
                }
        }
    }

    fun setUserOffline() {
        viewModelScope.launch {
            chatRepository.setUserOnlineStatus(false)
                .onFailure { e ->
                    Timber.e(e, "Failed to set user offline")
                }
        }
    }

    fun refreshMessages() {
        loadMessages()
    }
}

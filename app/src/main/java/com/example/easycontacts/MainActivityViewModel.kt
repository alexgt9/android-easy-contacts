package com.example.easycontacts

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easycontacts.data.model.Contact
import com.example.easycontacts.data.repository.ContactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val selectedUsername = savedStateHandle.getStateFlow(key = USERNAME, initialValue = "aleh")

    private val _syncContactsUiState = MutableStateFlow<SyncContactsUiState>(SyncContactsUiState.NotSynced)
    val syncContactsUiState = _syncContactsUiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val contactsLocalUiState: StateFlow<ListContactsUiState> = contactsRepository.getContacts(selectedUsername.value).flatMapLatest {
        if (it.isEmpty()) {
            flowOf(ListContactsUiState.Empty)
        } else {
            flowOf(ListContactsUiState.Success(it))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ListContactsUiState.Loading,
    )

    fun onSelectedUserChanged(username: String) {
        savedStateHandle[USERNAME] = username
    }

    init {
        loadContacts()
    }

    fun loadContacts(deletePrevious: Boolean = false) {
        syncContacts(deletePrevious)
    }

    private fun syncContacts(deletePrevious: Boolean = false) {
        viewModelScope.launch {
            try {
                _syncContactsUiState.value = SyncContactsUiState.Syncing
                contactsRepository.syncContacts(selectedUsername.value, deletePrevious)
                _syncContactsUiState.value = SyncContactsUiState.Synced
            } catch (e: Exception) {
                _syncContactsUiState.value =
                    SyncContactsUiState.Error("Error loading contacts: ${e.message}")
                Log.e("MainActivityViewModel", "Error loading contacts", e)
            }
        }
    }
}

sealed interface ListContactsUiState {
    data object Loading : ListContactsUiState
    data object Empty : ListContactsUiState
    data class Success(val contacts: List<Contact>) : ListContactsUiState
}

sealed interface SyncContactsUiState {
    data object NotSynced : SyncContactsUiState
    data object Syncing : SyncContactsUiState
    data object Synced : SyncContactsUiState
    data class Error(val message: String) : SyncContactsUiState
}

private const val USERNAME = "username"
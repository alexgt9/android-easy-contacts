package com.example.easycontacts

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easycontacts.repository.ContactsRepository
import com.example.easycontacts.ui.screens.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository,
) : ViewModel() {
    private val username = "aleh"

    private val _contactsUuiState = MutableStateFlow<ListContactsUiState>(ListContactsUiState.NotLoaded)
    val contactsUuiState: StateFlow<ListContactsUiState> = _contactsUuiState.asStateFlow()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            _contactsUuiState.value = ListContactsUiState.Loading
            val contacts = contactsRepository.getContacts(username)
            if (contacts.isEmpty()) {
                _contactsUuiState.value = ListContactsUiState.Empty
            } else {
                _contactsUuiState.value = ListContactsUiState.Success(contacts)
            }
        }
    }
}

sealed interface ListContactsUiState {
    data object NotLoaded : ListContactsUiState
    data object Loading : ListContactsUiState
    data object Empty : ListContactsUiState
    data class Success(val contacts: List<Contact>) : ListContactsUiState
}
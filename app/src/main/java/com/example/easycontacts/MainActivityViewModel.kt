package com.example.easycontacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easycontacts.repository.ContactsRepository
import com.example.easycontacts.model.Contact
import com.example.easycontacts.repository.ContactDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository,
    private val contactDao: ContactDao,
) : ViewModel() {
    private val username = "aleh"

    private val _contactsUuiState = MutableStateFlow<ListContactsUiState>(ListContactsUiState.NotLoaded)
    val contactsUuiState: StateFlow<ListContactsUiState> = _contactsUuiState.asStateFlow()

    init {
        loadContacts()
    }

    fun loadContacts() {
        viewModelScope.launch {
            try {
                _contactsUuiState.value = ListContactsUiState.Loading
                val contacts = contactsRepository.getContacts(username)
                if (contacts.isEmpty()) {
                    _contactsUuiState.value = ListContactsUiState.Empty
                } else {
                    _contactsUuiState.value = ListContactsUiState.Success(contacts)
                }
                contactDao.upsertContacts(contacts)
            } catch (e: Exception) {
                _contactsUuiState.value = ListContactsUiState.Error(e)
            }
        }
    }
}

sealed interface ListContactsUiState {
    data object NotLoaded : ListContactsUiState
    data object Loading : ListContactsUiState
    data object Empty : ListContactsUiState
    data class Success(val contacts: List<Contact>) : ListContactsUiState
    data class Error(val throwable: Throwable) : ListContactsUiState
}
package com.example.easycontacts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easycontacts.repository.ContactsRepository
import com.example.easycontacts.model.Contact
import com.example.easycontacts.repository.ContactDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository,
    private val contactDao: ContactDao,
) : ViewModel() {
    private val username = "aleh"

    @OptIn(ExperimentalCoroutinesApi::class)
    val contactsLocalUiState: StateFlow<ListContactsUiState> = contactDao.getContactsEntities().flatMapLatest {
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

    init {
        loadContacts()
    }

    fun loadContacts() {
        viewModelScope.launch {
            try {
                val contacts = contactsRepository.getContacts(username)
                contactDao.upsertContacts(contacts)
            } catch (e: Exception) {
                Log.e("MainActivityViewModel", "Error loading contacts", e)
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
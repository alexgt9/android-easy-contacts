package com.example.easycontacts.repository

import com.example.easycontacts.ui.screens.Contact
import javax.inject.Inject

class ContactsRepository @Inject constructor(
    private val contactsNetworkDataSource: ContactsNetworkDataSource,
) {
    suspend fun getContacts(username: String): List<Contact> {
        return contactsNetworkDataSource.getContacts(username)
    }
}
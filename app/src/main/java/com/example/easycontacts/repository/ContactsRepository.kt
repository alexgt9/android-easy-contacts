package com.example.easycontacts.repository

import com.example.easycontacts.model.Contact
import kotlinx.serialization.Serializable
import javax.inject.Inject

class ContactsRepository @Inject constructor(
    private val contactsNetworkDataSource: ContactsNetworkDataSource,
) {
    suspend fun getContacts(username: String): List<Contact> {
        return contactsNetworkDataSource.getContacts(username)
    }
}

@Serializable
data class NetworkContact(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val createdAt: String
)
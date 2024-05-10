package com.example.easycontacts.data.network

import com.example.easycontacts.data.model.Contact
import com.example.easycontacts.data.database.ContactDao
import com.example.easycontacts.data.repository.ContactsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import javax.inject.Inject

class OfflineFirstContactsRepository @Inject constructor(
    private val contactsNetworkDataSource: ContactsNetworkDataSource,
    private val contactDao: ContactDao,
) : ContactsRepository {
    override fun getContacts(username: String): Flow<List<Contact>> {
        return contactDao.getContactsEntities()
    }

    override suspend fun syncContacts(username: String, deletePrevious: Boolean) {
        if (deletePrevious) contactDao.deleteAllContacts()
        val contacts = contactsNetworkDataSource.getContacts(username)
        contactDao.upsertContacts(contacts)
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
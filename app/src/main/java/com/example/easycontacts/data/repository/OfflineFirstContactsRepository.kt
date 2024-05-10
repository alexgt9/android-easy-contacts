package com.example.easycontacts.data.repository

import com.example.easycontacts.data.model.Contact
import com.example.easycontacts.data.database.ContactDao
import com.example.easycontacts.data.network.NetworkContactsDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineFirstContactsRepository @Inject constructor(
    private val networkContactsDataSource: NetworkContactsDataSource,
    private val contactDao: ContactDao,
) : ContactsRepository {
    override fun getContacts(username: String): Flow<List<Contact>> {
        return contactDao.getContactsEntities()
    }
    override suspend fun syncContacts(username: String, deletePrevious: Boolean) {
        if (deletePrevious) contactDao.deleteAllContacts()
        val contacts = networkContactsDataSource.getContacts(username)
        contactDao.upsertContacts(contacts)
    }
}


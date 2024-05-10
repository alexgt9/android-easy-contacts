package com.example.easycontacts.data.repository

import com.example.easycontacts.data.model.Contact
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {
    fun getContacts(username: String): Flow<List<Contact>>

    suspend fun syncContacts(username: String, deletePrevious: Boolean = false)
}

@Module
@InstallIn(SingletonComponent::class)
internal interface ContactsRepositoryModule {
    @Binds
    fun providesContactsRepository(
        repository: OfflineFirstContactsRepository,
    ): ContactsRepository
}
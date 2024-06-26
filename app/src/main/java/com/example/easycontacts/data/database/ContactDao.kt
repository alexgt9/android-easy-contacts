package com.example.easycontacts.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.easycontacts.data.model.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query(value = "SELECT * FROM contacts")
    fun getContactsEntities(): Flow<List<Contact>>

    /**
     * Inserts or updates [entities] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertContacts(entities: List<Contact>)

    @Query("DELETE FROM contacts")
    suspend fun deleteAllContacts()
}
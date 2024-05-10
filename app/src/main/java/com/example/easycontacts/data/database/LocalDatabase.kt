package com.example.easycontacts.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.easycontacts.data.model.Contact
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(
    entities = [
        Contact::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
    UuidConverter::class,
)
internal abstract class LocalDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
}

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesDatabase(
        @ApplicationContext context: Context,
    ): LocalDatabase = Room.databaseBuilder(
        context,
        LocalDatabase::class.java,
        "easy-contacts-database",
    ).build()

    @Provides
    fun providesContactsDao(
        database: LocalDatabase,
    ): ContactDao = database.contactDao()
}
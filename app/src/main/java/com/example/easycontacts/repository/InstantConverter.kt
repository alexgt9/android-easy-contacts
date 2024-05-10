package com.example.easycontacts.repository

import androidx.room.TypeConverter
import java.time.Instant
import java.util.UUID

internal class InstantConverter {
    @TypeConverter
    fun longToInstant(value: Long?): Instant? =
        value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun instantToLong(instant: Instant?): Long? =
        instant?.toEpochMilli()
}

internal class UuidConverter {
    @TypeConverter
    fun stringToUuid(value: String): UUID = UUID.fromString(value)!!

    @TypeConverter
    fun uuidToString(uuid: UUID) = uuid.toString()
}
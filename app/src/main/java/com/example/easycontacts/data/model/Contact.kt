package com.example.easycontacts.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

@Entity(
    tableName = "contacts",
)
data class Contact(
    @PrimaryKey
    val id: UUID,
    val name: String,
    val phone: String,
    val email: String,
    val createdAt: Instant
)
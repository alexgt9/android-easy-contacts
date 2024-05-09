package com.example.easycontacts.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.UUID

@Composable
fun ContactsList(contacts: List<Contact>) {

    Column(modifier = Modifier.padding(16.dp)) {
        val modifier = Modifier.padding(4.dp)
        contacts.forEach { contact ->
            Card(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(modifier = modifier, text = contact.name)
                Text(modifier = modifier, text = contact.phone)
                Text(modifier = modifier, text = contact.email)
                Text(modifier = modifier, text = contact.createdAt.atZone(ZoneId.systemDefault()).toLocalDateTime().truncatedTo(ChronoUnit.MINUTES).toString())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactsListPreview() {
    ContactsList(
        listOf(
            Contact(
                UUID.randomUUID(),
                "John Doe",
                "1234567890",
                "email@example.com",
                Instant.now(),
            ),
            Contact(
                UUID.randomUUID(),
                "Jane Doe",
                "0987654321",
                "otro@example.com",
                Instant.now(),
            ),
        ),
    )
}

data class Contact(
    val id: UUID,
    val name: String,
    val phone: String,
    val email: String,
    val createdAt: Instant
)

package com.example.easycontacts.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.easycontacts.ListContactsUiState
import com.example.easycontacts.MainActivityViewModel
import com.example.easycontacts.model.Contact
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.UUID

@Composable
fun ContactsListRoute(currentUser: String, viewModel: MainActivityViewModel = hiltViewModel()) {
    ContactsScreen(
        contactsUiState = viewModel.contactsLocalUiState.collectAsState().value,
        viewModel::loadContacts,
    )
}

@Composable
fun ContactsScreen(contactsUiState: ListContactsUiState, onClickRefresh: () -> Unit) {
    when (contactsUiState) {
        ListContactsUiState.NotLoaded -> Text("Not loaded")
        ListContactsUiState.Loading -> Text("Loading")
        ListContactsUiState.Empty -> Text("No contacts found")
        is ListContactsUiState.Success -> {
            ContactsList(contacts = contactsUiState.contacts, onClickRefresh = onClickRefresh)
        }
    }
}

@Composable
fun ContactsList(contacts: List<Contact>, onClickRefresh: () -> Unit) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(contacts) { contact ->
            Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = contact.name)
                    Text(text = contact.phone)
                    Text(text = contact.email)
                    Text(text = contact.createdAt.atZone(ZoneId.systemDefault()).toLocalDateTime().truncatedTo(ChronoUnit.MINUTES).toString())
                }
            }
        }
        item {
            Button(onClick = onClickRefresh) {
                Text("Refresh")
            } }
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
        onClickRefresh = {},
    )
}

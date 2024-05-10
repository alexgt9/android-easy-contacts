package com.example.easycontacts.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.easycontacts.ListContactsUiState
import com.example.easycontacts.MainActivityViewModel
import com.example.easycontacts.SyncContactsUiState
import com.example.easycontacts.model.Contact
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.UUID

@Composable
fun ContactsListRoute(
    showSnackbar: (message: String) -> Unit,
    viewModel: MainActivityViewModel = hiltViewModel()
) {
    ContactsScreen(
        contactsUiState = viewModel.contactsLocalUiState.collectAsState().value,
        syncContactsUiState = viewModel.syncContactsUiState.collectAsState().value,
        viewModel::loadContacts,
        showSnackbar,
    )
}

@Composable
fun ContactsScreen(
    contactsUiState: ListContactsUiState,
    syncContactsUiState: SyncContactsUiState,
    onClickRefresh: () -> Unit,
    showSnackbar: (message: String) -> Unit
) {
    LaunchedEffect(syncContactsUiState) {
        if (syncContactsUiState == SyncContactsUiState.Synced) {
            showSnackbar("Synced")
        }
    }
    Box(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
        when (syncContactsUiState) {
            SyncContactsUiState.Syncing -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(20.dp)
                        .align(Alignment.TopEnd),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
            else -> {}
        }
        when (contactsUiState) {
            ListContactsUiState.NotLoaded -> Text("Not loaded")
            ListContactsUiState.Loading -> Text("Loading")
            ListContactsUiState.Empty -> {
                if (syncContactsUiState == SyncContactsUiState.Syncing) {
                    Text("Loading")
                } else {
                    Text("No contacts found")
                }
            }
            is ListContactsUiState.Success -> {
                ContactsList(contacts = contactsUiState.contacts, onClickRefresh = onClickRefresh)
            }
        }
    }
}

@Composable
fun ContactsList(contacts: List<Contact>, onClickRefresh: () -> Unit) {
    LazyColumn(modifier = Modifier.padding(8.dp)) {
        items(contacts) { contact ->
            Card(modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()) {
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
fun ContactsScreenPreview() {
    ContactsScreen(
        contactsUiState = ListContactsUiState.Success(
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
        ),
        syncContactsUiState = SyncContactsUiState.Syncing,
        onClickRefresh = {},
        showSnackbar = {},
    )
}

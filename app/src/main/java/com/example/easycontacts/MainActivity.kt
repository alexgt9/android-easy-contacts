package com.example.easycontacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.easycontacts.ui.components.UserDialog
import com.example.easycontacts.ui.screens.ContactsListRoute
import com.example.easycontacts.ui.theme.EasyContactsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }
            var showUserDialog by rememberSaveable { mutableStateOf(false) }
            val currentUser = viewModel.selectedUsername.collectAsState().value
            EasyContactsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    showUserDialog.let {
                        if (it) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Row {
                                    UserDialog(
                                        currentUser = currentUser,
                                        onCurrentUserChange = viewModel::onSelectedUserChanged,
                                        onDismissRequest = { showUserDialog = false; viewModel.loadContacts(deletePrevious = true) })
                                }
                            }
                        }
                    }
                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(hostState = snackbarHostState)
                        },
                        topBar = {
                            TopAppBar(
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.primary,
                                ),
                                title = {
                                    Text("Contacts of the user: $currentUser")
                                },
                                actions = {
                                    IconButton(onClick = { showUserDialog = true }) {
                                        Icon(
                                            imageVector = Icons.Filled.Person,
                                            contentDescription = "User"
                                        )
                                    }
                                },
                            )
                        },
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = it.calculateTopPadding())
                        ) {
                            ContactsListRoute(showSnackbar = { message ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}
package com.example.easycontacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.easycontacts.ui.screens.Contact
import com.example.easycontacts.ui.screens.ContactsList
import com.example.easycontacts.ui.screens.ContactsListRoute
import com.example.easycontacts.ui.theme.EasyContactsTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.util.UUID

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EasyContactsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    ContactsListRoute()
                }
            }
        }
    }
}
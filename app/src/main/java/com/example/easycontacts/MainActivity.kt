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
import com.example.easycontacts.ui.theme.EasyContactsTheme
import java.time.Instant
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EasyContactsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    ContactsList(contacts = listOf(
                        Contact(
                            id = UUID.randomUUID(),
                            name = "John Doe",
                            phone = "1234567890",
                            email = "aleh@example.com",
                            createdAt = Instant.now(),
                        ),
                    ))
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EasyContactsTheme {
        Greeting("Android")
    }
}
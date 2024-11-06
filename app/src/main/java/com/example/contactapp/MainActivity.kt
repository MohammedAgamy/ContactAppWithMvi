package com.example.contactapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.contactapp.view.screens.ConatctListScreen
import com.example.contactapp.viewModel.ContactViewModel

class MainActivity : ComponentActivity() {
    private val contactViewModel :ContactViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConatctListScreen(contactViewModel = contactViewModel)
        }
    }
}



package com.example.contactapp.view.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import java.nio.file.WatchEvent


@Composable
fun SearchInput(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchText: () -> Unit
) {
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = query,
        onValueChange = {
            onQueryChange(it)
            if (it.isEmpty()) {
                onSearchText()// Trigger the full user reload when the search field is cleared
            }

        },

        label = { Text(text = "Search Contact") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            onSearchText()
            softwareKeyboardController?.hide()
        })

    )
}
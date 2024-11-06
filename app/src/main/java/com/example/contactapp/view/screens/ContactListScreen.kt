package com.example.contactapp.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.contactapp.uimodel.ContactViewIntent
import com.example.contactapp.uimodel.SnackbarViewEffect
import com.example.contactapp.view.components.ContactItem
import com.example.contactapp.view.components.SearchInput
import com.example.contactapp.view.components.UserInPut
import com.example.contactapp.viewModel.ContactViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConatctListScreen(contactViewModel: ContactViewModel) {
    val viewState by contactViewModel.viewState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(contactViewModel) {
        contactViewModel.effectFlow.collect { effect ->
            when (effect) {
                is SnackbarViewEffect.ShowSnackbarView -> {
                    val result = snackbarHostState.showSnackbar(
                        message = effect.massage,
                        withDismissAction = true,
                        actionLabel = effect.action
                    )
                    if (result == SnackbarResult.ActionPerformed && effect.action == "Undo") {
                        contactViewModel.handleIntent(ContactViewIntent.UndoDelete)
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }
                }
            }

        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Contacts ", fontSize = 22.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EE))
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                SearchInput(
                    query = viewState.searchQuery,
                    onQueryChange = {
                        contactViewModel.handleIntent(
                            ContactViewIntent.SearchQueryContact(
                                it
                            )
                        )
                    },
                    onSearchText = { contactViewModel.handleIntent(ContactViewIntent.LoadContact) } // Reload all users when cleared
                )


                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    when {
                        viewState.isLoading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        viewState.contact.isEmpty() && !viewState.isLoading -> Text("No users available")
                        else -> {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                items(viewState.contact) { contact ->
                                    ContactItem(contactModel = contact, onDeleteUser = {
                                        contactViewModel.handleIntent(
                                            ContactViewIntent.DeleteContact(
                                                it
                                            )
                                        )
                                    })
                                }
                            }
                        }
                    }
                }
                UserInPut(
                    name = viewState.name,
                    email = viewState.email,
                    phone = viewState.phone,
                    nameError = viewState.nameError,
                    emailError = viewState.emailError,
                    phoneError = viewState.phoneError,
                    onNameChange = { contactViewModel.handleIntent(ContactViewIntent.UpdateName(it)) },
                    onEmailChange = { contactViewModel.handleIntent(ContactViewIntent.UpdateEmail(it)) },
                    onPhoneChange = { contactViewModel.handleIntent(ContactViewIntent.UpdatePhone(it)) },
                    onAddUser = {
                        contactViewModel.handleIntent(
                            ContactViewIntent.AddContactView(
                                it.first,
                                it.second,
                                it.third
                            )
                        )
                        scope.launch {
                            listState.animateScrollToItem(viewState.contact.size) // Scroll to the last added user
                        }
                    },
                    onClearUsers = { contactViewModel.handleIntent(ContactViewIntent.ClearContact) }
                )


            }
        }
    )

}

class MockUserListViewModel : ContactViewModel()


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val viewModel = MockUserListViewModel() // Use mock data instead of the real ViewModel
    ConatctListScreen(contactViewModel = viewModel)
}
package com.example.contactapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactapp.model.ContactModel
import com.example.contactapp.repo.ContactRepository
import com.example.contactapp.repo.ContactRepositoryImp
import com.example.contactapp.uimodel.ContactViewIntent
import com.example.contactapp.uimodel.ContactViewState
import com.example.contactapp.uimodel.SnackbarViewEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ContactViewModel : ViewModel() {

    val repository: ContactRepository = ContactRepositoryImp()

    private val _viewState = MutableStateFlow(ContactViewState())
    val viewState: StateFlow<ContactViewState> = _viewState

    private val _effectChannel = Channel<SnackbarViewEffect>()
    val effectFlow: Flow<SnackbarViewEffect> = _effectChannel.receiveAsFlow()


    private val recentlyDeletedContact: ContactViewModel? = null

    init {
        handleIntent(ContactViewIntent.LoadContact)
    }


    fun handleIntent(intent: ContactViewIntent) {
        when (intent) {
            is ContactViewIntent.LoadContact -> loadContact()
            is ContactViewIntent.AddContactView -> addContact(
                intent.name,
                intent.email,
                intent.phone
            )
            is ContactViewIntent.DeleteContact ->

        }
    }


    private fun loadContact() {
        _viewState.value = _viewState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val contact = repository.getContact()

                _viewState.value = _viewState.value.copy(
                    isLoading = false,
                    contact = contact
                )

                _effectChannel.send(
                    SnackbarViewEffect.ShowSnackbarView(
                        if (contact.isEmpty()) "no number available " else "Number Loaded"
                    )
                )
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(
                    isLoading = false,
                )
                _effectChannel.send(
                    SnackbarViewEffect.ShowSnackbarView(
                        e.message ?: "error"
                    )
                )
            }
        }

    }


    private fun addContact(name: String, email: String, phone: String) {
        val nameError = name.isBlank()
        val emailError =
            email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val phoneError = phone.isBlank() || !android.util.Patterns.PHONE.matcher(phone).matches()

        if (nameError || emailError || phoneError) {
            _viewState.value = _viewState.value.copy(
                nameError = nameError,
                emailError = emailError,
                phoneError = phoneError
            )

            return
        }


        _viewState.value = _viewState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val contacts = ContactModel(id = (0..1000).random(), name, email, phone)
                val addrepo = repository.addContact(contacts)
                _viewState.value = _viewState.value.copy(
                    isLoading = true,
                    contact = addrepo,
                    name = "",
                    email = "",
                    phone = "",
                )

                _effectChannel.send(SnackbarViewEffect.ShowSnackbarView("Contact added successfully!"))

            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(isLoading = false)
                _effectChannel.send(SnackbarViewEffect.ShowSnackbarView("Error adding Contact: ${e.message}"))
            }
        }
    }




}
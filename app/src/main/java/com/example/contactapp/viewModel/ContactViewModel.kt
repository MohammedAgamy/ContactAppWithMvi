package com.example.contactapp.viewModel

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

open class ContactViewModel : ViewModel() {

    val repository: ContactRepository = ContactRepositoryImp()

    private val _viewState = MutableStateFlow(ContactViewState())
    val viewState: StateFlow<ContactViewState> = _viewState

    private val _effectChannel = Channel<SnackbarViewEffect>()
    val effectFlow: Flow<SnackbarViewEffect> = _effectChannel.receiveAsFlow()


    private var recentlyDeletedContact: ContactModel? = null

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

            is ContactViewIntent.DeleteContact -> deleteContact(intent.contactModel)
            is ContactViewIntent.UndoDelete -> undoDelete()
            is ContactViewIntent.ClearContact -> clearContact()
            is ContactViewIntent.SearchQueryContact -> searchQueryContact(intent.query)
            is ContactViewIntent.UpdateEmail -> updateEmail(intent.email)
            is ContactViewIntent.UpdateName -> updateName(intent.name)
            is ContactViewIntent.UpdatePhone -> updatePhone(intent.phone)
        }
    }

    private fun updatePhone(phone: String) {
        _viewState.value = _viewState.value.copy(phone = phone, phoneError = false)

    }

    private fun updateName(name: String) {
        _viewState.value = _viewState.value.copy(name = name, nameError = false)

    }

    private fun updateEmail(email: String) {
        _viewState.value = _viewState.value.copy(email = email, emailError = false)

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
                    isLoading = false,
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

    private fun deleteContact(contactModel: ContactModel) {
        _viewState.value = _viewState.value.copy(
            isLoading = true
        )


        viewModelScope.launch {
            recentlyDeletedContact = contactModel
            try {
                val contactRepository = repository.deleteContact(contactModel)
                _viewState.value =
                    _viewState.value.copy(isLoading = false, contact = contactRepository)
                _effectChannel.send(SnackbarViewEffect.ShowSnackbarView("Contact delete successfully!"))

            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(isLoading = false)
                _effectChannel.send(SnackbarViewEffect.ShowSnackbarView("Error deleting contact: ${e.message}"))
            }
        }
    }

    private fun undoDelete() {
        _viewState.value = _viewState.value.copy(
            isLoading = true
        )
        recentlyDeletedContact?.let { deleteContact ->
            addContact(deleteContact.name, deleteContact.email, deleteContact.phone)
            recentlyDeletedContact = null
        }

    }

    private fun clearContact() {
        _viewState.value = _viewState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val repoContact = repository.clearContact()
                _viewState.value = _viewState.value.copy(isLoading = false, contact = repoContact)
                _effectChannel.send(SnackbarViewEffect.ShowSnackbarView("All Contact cleared!"))
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(isLoading = false)
                _effectChannel.send(SnackbarViewEffect.ShowSnackbarView("Error clearing Contact: ${e.message}"))
            }
        }
    }

    private fun searchQueryContact(query: String) {
        _viewState.value = _viewState.value.copy(searchQuery = query)

        viewModelScope.launch {
            if (query.isBlank()) {
                val allContact = repository.getContact()
                _viewState.value = _viewState.value.copy(contact = allContact)
            } else {
                val filteredContact = _viewState.value.contact.filter {
                    it.name.contains(query, ignoreCase = true) || it.email.contains(
                        query,
                        ignoreCase = true
                    ) || it.phone.contains(query, ignoreCase = true)
                }
                _viewState.value = _viewState.value.copy(contact = filteredContact)
                if (filteredContact.isEmpty()) {
                    _effectChannel.send(SnackbarViewEffect.ShowSnackbarView("No Contact found"))
                }
            }
        }
    }


}
package com.example.contactapp.uimodel

import com.example.contactapp.model.ContactModel

data class ContactViewState(
    val isLoading: Boolean = false,
    val contact: List<ContactModel> = emptyList(),
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val nameError: Boolean = false,
    val emailError: Boolean = false,
    val phoneError: Boolean = false,
    val searchQuery: String = "",
    val recentlyDeletedUser: ContactModel? = null

)
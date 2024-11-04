package com.example.contactapp.uimodel

import com.example.contactapp.model.ContactModel

sealed class ContactViewIntent {
    data object LoadContact : ContactViewIntent()
    data class AddContactView(val name: String, val email: String, val phone: String) :
        ContactViewIntent()

    data class DeleteContact(val contactModel: ContactModel) : ContactViewIntent()
    data object ClearContact : ContactViewIntent()
    data class SearchQueryContact(val query: String) : ContactViewIntent()
    data class UpdateName(val name: String) : ContactViewIntent()
    data class UpdateEmail(val email: String) : ContactViewIntent()
    data class UpdatePhone(val phone: String) : ContactViewIntent()
    data object UndoDelete : ContactViewIntent()

}
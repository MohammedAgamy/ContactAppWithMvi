package com.example.contactapp.repo

import com.example.contactapp.model.ContactModel

interface ContactRepository {
    suspend fun getContact():List<ContactModel>
    suspend fun addContact(contactModel: ContactModel):List<ContactModel>
    suspend fun deleteContact(contactModel: ContactModel):List<ContactModel>
    suspend fun clearContact():List<ContactModel>
}
package com.example.contactapp.repo

import com.example.contactapp.model.ContactModel
import kotlinx.coroutines.delay

class ContactRepositoryImp : ContactRepository {

    private var contactUser = mutableListOf<ContactModel>()

    override suspend fun getContact(): List<ContactModel> {
        delay(3000)
        return contactUser
    }

    override suspend fun addContact(contactModel: ContactModel): List<ContactModel> {
        delay(3000)
        contactUser.add(contactModel)
        return contactUser
    }

    override suspend fun deleteContact(contactModel: ContactModel): List<ContactModel> {
        delay(3000)
        contactUser.remove(contactModel)
        return contactUser
    }

    override suspend fun clearContact(): List<ContactModel> {
        delay(3000)
        contactUser.clear()
        return contactUser
    }
}
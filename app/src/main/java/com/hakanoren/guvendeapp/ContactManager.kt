package com.hakanoren.guvendeapp.models

object ContactManager {
    private val contacts: MutableList<Contact> = mutableListOf()

    fun addContact(contact: Contact) {
        contacts.add(contact)
    }

    fun removeContact(contact: Contact) {
        contacts.remove(contact)
    }

    fun getAllContacts(): List<Contact> {
        return contacts
    }
}

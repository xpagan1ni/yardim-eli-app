package com.hakanoren.guvendeapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hakanoren.guvendeapp.models.Contact

class ContactViewModel : ViewModel() {
    val contactList = MutableLiveData<MutableList<Contact>>(mutableListOf())
}
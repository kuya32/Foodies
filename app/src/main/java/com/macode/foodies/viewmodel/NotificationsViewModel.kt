package com.macode.foodies.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationsViewModel:ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is library!"
    }

    val text: LiveData<String> = _text
}
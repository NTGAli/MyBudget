package com.ntg.core.mybudget.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    var loginEventListener: LoginEventListener? = null

    val bottomNavTitle = MutableLiveData<String>()
    val setExpand = MutableLiveData<Boolean>()

    fun sendLoginEvent() {
        loginEventListener?.onLoginEvent()
    }
}
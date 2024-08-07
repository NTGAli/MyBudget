package com.ntg.core.mybudget.common

import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    var loginEventListener: LoginEventListener? = null

    fun sendLoginEvent() {
        loginEventListener?.onLoginEvent()
    }
}
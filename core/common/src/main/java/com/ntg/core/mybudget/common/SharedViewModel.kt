package com.ntg.core.mybudget.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ntg.core.model.res.Currency

class SharedViewModel : ViewModel() {
    var loginEventListener: LoginEventListener? = null

    val bottomNavTitle = MutableLiveData<String>()
    val bottomNavIcon = MutableLiveData<Int>()
    val setExpand = MutableLiveData<Boolean>()
    val setLoading = MutableLiveData<Boolean>()

    fun sendLoginEvent() {
        loginEventListener?.onLoginEvent()
    }
}
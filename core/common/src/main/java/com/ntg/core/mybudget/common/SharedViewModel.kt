package com.ntg.core.mybudget.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
     var bottomButtonListener: BottomButtonListener? = null
    var openTransactionOnHome = false

    val bottomNavTitle = MutableLiveData<String>()
    val bottomNavIcon = MutableLiveData<Int>()
    val setExpand = MutableLiveData<Boolean>()
    val setLoading = MutableLiveData<Boolean>()

    fun onBottomButtonClick(): Boolean {
        val listener = bottomButtonListener
        if (listener != null) {
            listener.onBottomButtonClick()
            return true
        }
        return false
    }
}

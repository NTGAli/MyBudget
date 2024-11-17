package com.ntg.features.profile.appearance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.core.data.repository.UserDataRepository
import com.ntg.core.model.ThemeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppearanceViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    val userData = userDataRepository.userData

    fun setTheme(themeState: ThemeState)  {
        viewModelScope.launch {
            userDataRepository.changeTheme(themeState)
        }
    }
}
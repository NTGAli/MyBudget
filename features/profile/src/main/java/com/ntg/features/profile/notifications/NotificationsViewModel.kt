package com.ntg.features.profile.notifications

import androidx.lifecycle.ViewModel
import com.ntg.core.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
): ViewModel() {

}
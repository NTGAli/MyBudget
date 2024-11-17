package com.ntg.features.profile.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.core.data.repository.ProfileRepository
import com.ntg.core.model.res.SessionsResItem
import com.ntg.core.network.model.ResponseBody
import com.ntg.core.network.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _sessionsState = MutableStateFlow<Result<List<SessionsResItem>>>(Result.Loading(false))
    val sessionsState = _sessionsState

    private val _terminateState = MutableStateFlow<Result<ResponseBody<String?>>>(Result.Loading(false))
    val terminateState: StateFlow<Result<ResponseBody<String?>>> = _terminateState

    init {
        getAllSessions()
    }

    fun getAllSessions() {
        viewModelScope.launch {
            _sessionsState.value = Result.Loading(true)

            profileRepository.getSessionsList().collect {
                _sessionsState.value = it
            }
        }
    }

    fun terminateAllSessions() {
        viewModelScope.launch {
            profileRepository.terminateAllSessions().collect {
                _terminateState.value = it
            }
        }
    }

    fun terminateSession(sessionId: String) {
        viewModelScope.launch {
            profileRepository.terminateSession(sessionId).collect {
                _terminateState.value = it
            }
        }
    }
}
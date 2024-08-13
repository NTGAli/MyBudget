package com.ntg.features.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.core.data.repository.AccountRepository
import com.ntg.core.data.repository.SourceExpenditureRepository
import com.ntg.core.model.Account
import com.ntg.core.model.SourceExpenditure
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel
@Inject constructor(
    private val accountRepository: AccountRepository,
    private val sourceRepository: SourceExpenditureRepository
) : ViewModel() {

    fun accounts() = accountRepository.getAll()

    fun insertNewAccount(
        account: Account
    ){
        viewModelScope.launch {
            accountRepository.insert(account)
        }
    }


    fun insertNewSource(
        source: SourceExpenditure
    ){
        viewModelScope.launch {
            sourceRepository.insert(source)
        }
    }

}
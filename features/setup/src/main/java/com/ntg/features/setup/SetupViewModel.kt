package com.ntg.features.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.core.data.repository.AccountRepository
import com.ntg.core.data.repository.BankCardRepository
import com.ntg.core.data.repository.SourceExpenditureRepository
import com.ntg.core.data.repository.transaction.TransactionsRepository
import com.ntg.core.model.Account
import com.ntg.core.model.SourceExpenditure
import com.ntg.core.model.SourceType
import com.ntg.core.model.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel
@Inject constructor(
    private val accountRepository: AccountRepository,
    private val sourceRepository: SourceExpenditureRepository,
    private val bankCardRepository: BankCardRepository,
    private val transactionRepository: TransactionsRepository
) : ViewModel() {

    fun accounts() = accountRepository.getAll()

    fun getAccount(id: Int) = accountRepository.getAccountByAccount(id)

    fun accountWithSources() = accountRepository.getAccountsWithSources()

    fun getSourcesById(id: Int) = sourceRepository.getSourceDetails(id)

    fun upsertAccount(account: Account) {
        viewModelScope.launch {
            account.dateModified = System.currentTimeMillis().toString()
            if (account.dateCreated.orEmpty().isEmpty()){
                account.dateCreated = System.currentTimeMillis().toString()
            }
            accountRepository.update(account)
        }
    }

    fun updateAccount(account: Account){
        viewModelScope.launch {
            account.dateModified = System.currentTimeMillis().toString()
            account.dateCreated = System.currentTimeMillis().toString()
            accountRepository.update(account)
        }
    }

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

    fun insertNewBankCard(
        bankCard: SourceType.BankCard
    ){
        viewModelScope.launch {
            bankCardRepository.insert(bankCard)
        }
    }

    fun updateBankCard(bankCard: SourceType.BankCard) {
        viewModelScope.launch {
            bankCardRepository.update(bankCard)
        }
    }

    fun tempRemove(sourceId: Int) {
        viewModelScope.launch {
            sourceRepository.tempRemove(sourceId)
        }
    }

    fun initCardTransactions(
        initAmount: Long,
        sourceId: Int,
        accountId: Int
    ){
        viewModelScope.launch {
            transactionRepository.insertNewTransaction(
                Transaction(
                    0, amount = initAmount, accountId = accountId, sourceId = sourceId, date = System.currentTimeMillis()
                )
            )
        }
    }

}
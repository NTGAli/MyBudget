package com.ntg.features.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ntg.core.data.repository.AccountRepository
import com.ntg.core.data.repository.BankCardRepository
import com.ntg.core.data.repository.CategoryRepository
import com.ntg.core.data.repository.ConfigRepository
import com.ntg.core.data.repository.ContactRepository
import com.ntg.core.data.repository.WalletsRepository
import com.ntg.core.data.repository.transaction.TransactionsRepository
import com.ntg.core.model.Contact
import com.ntg.core.model.Transaction
import com.ntg.core.model.Wallet
import com.ntg.core.model.res.Bank
import com.ntg.core.model.res.Category
import com.ntg.core.model.res.Currency
import com.ntg.core.model.res.ServerConfig
import com.ntg.core.mybudget.common.Constants
import com.ntg.core.mybudget.common.logd
import com.ntg.mybudget.sync.work.workers.initializers.Sync
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val sourceRepository: WalletsRepository,
    private val transactionsRepository: TransactionsRepository,
    private val categoryRepository: CategoryRepository,
    private val configRepository: ConfigRepository,
    private val bankCardRepository: BankCardRepository,
    private val contactRepository: ContactRepository
    ): ViewModel() {

    private val _walletTypes = MutableStateFlow<List<Wallet>?>(emptyList())
    private val _currency = MutableStateFlow<Currency?>(null)
    private val _categories = MutableStateFlow<List<Category>?>(emptyList())
    private val _localUserBanks = MutableStateFlow<List<Bank>?>(emptyList())
    private val _contacts = MutableStateFlow<List<Contact>?>(emptyList())

    private val _transaction = MutableStateFlow<Transaction?>(null)

    fun selectedAccount() = accountRepository.getSelectedAccount()

    fun selectedSources(): MutableStateFlow<List<Wallet>?> {
        viewModelScope.launch {
            sourceRepository.getSelectedSources().collect{
                _walletTypes.value = it
            }
        }
        return _walletTypes
    }

    fun currencyInfo(): MutableStateFlow<Currency?> {
        viewModelScope.launch {
            sourceRepository.getCurrentCurrency().collect{
                _currency.value = it
            }
        }
        return _currency
    }

    fun getContacts(): MutableStateFlow<List<Contact>?> {
        viewModelScope.launch {
            contactRepository.getAll().collect{
                _contacts.value = it
            }
        }
        return _contacts
    }

    fun transactions(sourceIds: List<Int>) = transactionsRepository.getTransactionsBySourceIds(sourceIds)

    fun accountWithSources() = accountRepository.getAccountsWithSources()

    fun updatedSelectedAccount(accountId: Int) {
        viewModelScope.launch {
            accountRepository.updateSelectedAccountAndSources(accountId)
        }
    }

    fun updatedSelectedSources(sourceIds: List<Int>) {
        viewModelScope.launch {
            sourceRepository.updateSelectedSources(sourceIds)
        }
    }

    fun getBankLogoColor(): Flow<ServerConfig?> {
        return configRepository.get(Constants.Configs.BANK_LOGO_COLOR_URL)
    }

    fun getCategories(): MutableStateFlow<List<Category>?> {
        viewModelScope.launch {
            categoryRepository.getCategories().collect{
                _categories.value = it
            }
        }
        return _categories
    }

    fun insertTransaction(transaction: Transaction){
        viewModelScope.launch {
            transactionsRepository.insertNewTransaction(transaction)
        }
    }


    fun updateTransaction(transaction: Transaction){
        viewModelScope.launch {
            transactionsRepository.updateTransaction(transaction)
        }
    }

    fun transactionById(id: Int): MutableStateFlow<Transaction?>{
        viewModelScope.launch {
            transactionsRepository.transactionById(id).collect{transaction ->
                contactRepository.get(transaction?.contactIds).collect{contactsData ->
                    transaction?.apply {
                        contacts = contactsData.orEmpty()
                        _transaction.value = transaction

                    }
                }
            }
        }
        return _transaction
    }

    private fun parseContactsJson(contactsJson: String?): List<Contact> {
        return if (!contactsJson.isNullOrEmpty()) {
            try {
                val type = object : TypeToken<List<Contact>>() {}.type
                Gson().fromJson(contactsJson, type)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }
    fun getLocalUserBanks(): MutableStateFlow<List<Bank>?> {
        viewModelScope.launch {
            bankCardRepository.getUserLocalBanks().collect{
                _localUserBanks.value = it
            }
        }
        return _localUserBanks
    }

    fun tempRemoveWallet(sourceId: Int, context: Context?) {
        viewModelScope.launch {
            sourceRepository.tempRemove(sourceId)
        }
        if (context != null) {
            Sync.initialize(context = context)
        }
    }

    fun deleteAccount(accountId: Int, context: Context? = null){
        viewModelScope.launch {
            accountRepository.delete(accountId)
        }
        if (context != null) {
            Sync.initialize(context = context)
        }
    }

    fun deleteTransaction(id: Int){
        viewModelScope.launch {
            transactionsRepository.deleteTransaction(id)
        }
    }

    fun insertContact(contact: Contact){
        viewModelScope.launch {
            contactRepository.upsertContact(contact)
        }
    }
}
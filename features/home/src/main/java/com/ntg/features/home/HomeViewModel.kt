package com.ntg.features.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ntg.core.data.repository.*
import com.ntg.core.data.repository.transaction.TransactionsRepository
import com.ntg.core.model.AccountWithSources
import com.ntg.core.model.Contact
import com.ntg.core.model.Transaction
import com.ntg.core.model.TransactionFilter
import com.ntg.core.model.Wallet
import com.ntg.core.model.res.Bank
import com.ntg.core.model.res.Category
import com.ntg.core.model.res.Currency
import com.ntg.core.model.res.ServerConfig
import com.ntg.core.mybudget.common.Constants
import com.ntg.core.mybudget.common.orDefault
import com.ntg.mybudget.sync.work.workers.initializers.Sync
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
) : ViewModel() {

    // Private MutableStateFlows
    private val _selectedAccount = MutableStateFlow<List<AccountWithSources>?>(null)
    private val _selectedSources = MutableStateFlow<List<Wallet>?>(emptyList())
    private val _currency = MutableStateFlow<Currency?>(null)
    private val _categories = MutableStateFlow<List<Category>?>(null)
    private val _localUserBanks = MutableStateFlow<List<Bank>?>(emptyList())
    private val _contacts = MutableStateFlow<List<Contact>?>(emptyList())
    private val _transaction = MutableStateFlow<Transaction?>(null)
    private val _bankLogoColor = MutableStateFlow<ServerConfig?>(null)
    private val _transactionFilter = MutableStateFlow(TransactionFilter())
    val transactionFilter: StateFlow<TransactionFilter> = _transactionFilter

    // Public exposed StateFlows
    val selectedAccount: StateFlow<List<AccountWithSources>?> = _selectedAccount
    val selectedSources: StateFlow<List<Wallet>?> = _selectedSources
    val currency: StateFlow<Currency?> = _currency
    val categories: StateFlow<List<Category>?> = _categories
    val localUserBanks: StateFlow<List<Bank>?> = _localUserBanks
    val contacts: StateFlow<List<Contact>?> = _contacts
    val transaction: StateFlow<Transaction?> = _transaction
    val bankLogoColor: StateFlow<ServerConfig?> = _bankLogoColor
    private val _filteredTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val filteredTransactions: StateFlow<List<Transaction>> = _filteredTransactions
    private val _isFiltered = MutableStateFlow(false)
    val isFiltered: StateFlow<Boolean> = _isFiltered

    init {
        viewModelScope.launch {
            bankCardRepository.getUserLocalBanks().collect {
                _localUserBanks.value = it
            }
        }

        viewModelScope.launch {
            categoryRepository.getCategories().collect {
                _categories.value = it
            }
        }

        viewModelScope.launch {
            accountRepository.getSelectedAccount().collect {
                _selectedAccount.value = it
            }
        }

        viewModelScope.launch {
            sourceRepository.getSelectedSources().collect {
                _selectedSources.value = it
            }
        }

        viewModelScope.launch {
            sourceRepository.getCurrentCurrency().collect {
                _currency.value = it
            }
        }

        viewModelScope.launch {
            contactRepository.getAll().collect {
                _contacts.value = it
            }
        }

        viewModelScope.launch {
            configRepository.get(Constants.Configs.BANK_LOGO_COLOR_URL).collect {
                _bankLogoColor.value = it
            }
        }


        viewModelScope.launch {
            _selectedSources.collect {
                refreshFilteredTransactions()
            }
        }
    }

    private val _scrollState = MutableStateFlow(0)
    val scrollState: StateFlow<Int> = _scrollState

    fun updateScrollPosition(firstVisibleItemIndex: Int) {
        _scrollState.value = firstVisibleItemIndex
    }

    fun accountWithSources() = accountRepository.getAccountsWithSources()

    fun transactions(sourceIds: List<Int>) = transactionsRepository.getTransactionsBySourceIds(sourceIds)

    fun updatedSelectedAccount(accountId: Int) {
        viewModelScope.launch {
            accountRepository.updateSelectedAccountAndSources(accountId)
        }
    }


    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionsRepository.insertNewTransaction(transaction)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionsRepository.updateTransaction(transaction)
        }
    }

    fun loadTransactionById(id: Int) {
        viewModelScope.launch {
            transactionsRepository.transactionById(id).collect { transaction ->
                if (transaction != null) {
                    contactRepository.get(transaction.contactIds).collect { contactsData ->
                        transaction.apply {
                            contacts = contactsData.orEmpty()
                            _transaction.value = this
                        }
                    }
                } else {
                    _transaction.value = null
                }
            }
        }
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

    fun tempRemoveWallet(sourceId: Int, context: Context?) {
        viewModelScope.launch {
            sourceRepository.tempRemove(sourceId)
        }
        if (context != null) {
            Sync.initialize(context = context)
        }
    }

    fun deleteAccount(accountId: Int, context: Context? = null) {
        viewModelScope.launch {
            accountRepository.delete(accountId)
        }
        if (context != null) {
            Sync.initialize(context = context)
        }
    }

    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            transactionsRepository.deleteTransaction(id)
        }
    }

    fun insertContact(contact: Contact) {
        viewModelScope.launch {
            contactRepository.upsertContact(contact)
        }
    }

    fun applyTransactionFilter(filter: TransactionFilter) {
        _transactionFilter.value = filter
        _isFiltered.value = isFilterActive(filter)

        // Re-fetch transactions with the new filter
        refreshFilteredTransactions()
    }

    private fun isFilterActive(filter: TransactionFilter): Boolean {
        return filter.type != null ||
                filter.dateFrom != null ||
                filter.dateTo != null ||
                filter.categoryId != null ||
                filter.tags.isNotEmpty() ||
                filter.hasImage
    }

    private fun refreshFilteredTransactions() {
        viewModelScope.launch {
            val sourceIds = _selectedSources.value?.map { it.id } ?: emptyList()
            if (sourceIds.isEmpty()) {
                _filteredTransactions.value = emptyList()
                return@launch
            }

            transactionsRepository.getTransactionsBySourceIds(sourceIds).collect { allTransactions ->
                val filteredList = filterTransactions(allTransactions, _transactionFilter.value)
                _filteredTransactions.value = filteredList
            }
        }
    }

    private fun filterTransactions(transactions: List<Transaction>, filter: TransactionFilter): List<Transaction> {
        return transactions.filter { transaction ->
            var matches = true

            // Filter by type
            if (filter.type != null) {
                matches = matches && transaction.type == filter.type
            }

            // Filter by date range
            if (filter.dateFrom != null) {
                matches = matches && transaction.date >= filter.dateFrom.orDefault()
            }

            if (filter.dateTo != null) {
                matches = matches && transaction.date <= filter.dateTo.orDefault()
            }

            // Filter by category
            if (filter.categoryId != null) {
                matches = matches && transaction.categoryId == filter.categoryId
            }

            // Filter by tags - match any of the filter tags
            if (filter.tags.isNotEmpty()) {
                matches = matches && transaction.tags.orEmpty().any { it in filter.tags }
            }

            // Filter by has image
            if (filter.hasImage) {
                matches = matches && !transaction.images.isNullOrEmpty()
            }

            matches
        }
    }

    fun clearTransactionFilters() {
        _transactionFilter.value = TransactionFilter()
        _isFiltered.value = false
        refreshFilteredTransactions()
    }

    fun updatedSelectedSources(sourceIds: List<Int>) {
        viewModelScope.launch {
            sourceRepository.updateSelectedSources(sourceIds)
            // After updating sources, refresh filtered transactions
            refreshFilteredTransactions()
        }
    }
}
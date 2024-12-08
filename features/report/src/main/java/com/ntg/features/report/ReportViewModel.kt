package com.ntg.features.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.core.data.repository.AccountRepository
import com.ntg.core.data.repository.CategoryRepository
import com.ntg.core.data.repository.WalletsRepository
import com.ntg.core.data.repository.transaction.TransactionsRepository
import com.ntg.core.model.res.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val walletsRepository: WalletsRepository,
    private val transactionsRepository: TransactionsRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _selectedWalletIds = MutableStateFlow<List<Int>?>(emptyList())
    private val _categories = MutableStateFlow<List<Category>?>(emptyList())

    fun selectedWalletsIds(): MutableStateFlow<List<Int>?> {
        viewModelScope.launch {
            walletsRepository.getSelectedWalletIds().collect{
                _selectedWalletIds.value = it
            }
        }
        return _selectedWalletIds
    }

    fun transactions(sourceIds: List<Int>) = transactionsRepository.getTransactionsBySourceIds(sourceIds)


    fun getCategories(): MutableStateFlow<List<Category>?> {
        viewModelScope.launch {
            categoryRepository.getCategories().collect{
                _categories.value = it
            }
        }
        return _categories
    }

}
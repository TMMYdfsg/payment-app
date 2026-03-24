package com.payment.app.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payment.app.data.db.entity.BankAccountEntity
import com.payment.app.data.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountManageUiState(
    val accounts: List<BankAccountEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AccountManageViewModel @Inject constructor(
    private val repository: PaymentRepository
) : ViewModel() {

    val uiState: StateFlow<AccountManageUiState> = repository.allAccounts
        .map { accounts ->
            AccountManageUiState(
                accounts = accounts,
                isLoading = false
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AccountManageUiState())

    init {
        viewModelScope.launch {
            repository.initializeDefaultAccounts()
        }
    }

    fun addAccount(accountName: String, bankName: String) {
        if (accountName.isBlank()) return
        viewModelScope.launch {
            repository.addAccount(accountName, bankName)
        }
    }

    fun deleteAccount(account: BankAccountEntity) {
        viewModelScope.launch {
            repository.deleteAccount(account)
        }
    }
}

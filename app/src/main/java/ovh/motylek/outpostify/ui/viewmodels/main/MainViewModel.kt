package ovh.motylek.outpostify.ui.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel
import ovh.motylek.outpostify.data.database.entities.AccountEntity
import ovh.motylek.outpostify.data.repository.AccountRepository
import ovh.motylek.outpostify.ui.routes.Route

@KoinViewModel
class MainViewModel(
    private val accountRepository: AccountRepository
) : ViewModel() {
    val currentAccount = accountRepository.getCurrentAccount()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val accounts = accountRepository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val userSwitcherVisible = MutableStateFlow(false)

    suspend fun getStartDestination() = if (accountRepository.getCurrentAccount().first() != null) Route.Parcels else Route.Welcome

    fun showUserSwitcher() {
        userSwitcherVisible.value = true
    }
    fun hideUserSwitcher() {
        userSwitcherVisible.value = false
    }

    suspend fun switchUser(account: AccountEntity) {
        accountRepository.switchAccount(account)
        hideUserSwitcher()
    }
}
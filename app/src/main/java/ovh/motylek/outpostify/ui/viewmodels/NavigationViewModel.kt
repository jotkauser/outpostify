package ovh.motylek.outpostify.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.annotation.KoinViewModel
import ovh.motylek.outpostify.data.database.entities.AccountEntity
import ovh.motylek.outpostify.data.repository.AccountRepository
import ovh.motylek.outpostify.service.ClientManager
import ovh.motylek.outpostify.ui.routes.Parcels
import ovh.motylek.outpostify.ui.routes.Welcome

@KoinViewModel
class NavigationViewModel(
    private val accountRepository: AccountRepository,
    private val clientManager: ClientManager
) : ViewModel() {
    val userId = MutableStateFlow(0)
    val accounts = MutableStateFlow(listOf<AccountEntity>())
    val userSwitcherVisible = MutableStateFlow(false)

    fun getStartDestination(): Any = runBlocking {
        if (accountRepository.getAllAccounts().isEmpty()) {
            return@runBlocking Welcome
        } else {
            val account = accountRepository.getCurrentAccount()
            userId.value = account.id.toInt()
            return@runBlocking Parcels(account.id)
        }
    }

    init {
        viewModelScope.launch {
            userId.collect {
                if (it == 0) {
                    return@collect
                }
                clientManager.initApiClient(it.toLong())
            }
        }
    }

    fun showUserSwitcher() {
        userSwitcherVisible.value = true
        viewModelScope.launch {
            accounts.value = accountRepository.getAllAccounts()
        }
    }

    fun hideUserSwitcher() {
        userSwitcherVisible.value = false
    }

    suspend fun switchUser(account: AccountEntity) {
        accountRepository.switchAccount(account)
        userId.value = account.id.toInt()
        userSwitcherVisible.value = false
    }
}
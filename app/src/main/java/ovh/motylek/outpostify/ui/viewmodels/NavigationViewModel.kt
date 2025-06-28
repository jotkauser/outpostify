package ovh.motylek.outpostify.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
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
    val userSwtitcherVisible = MutableStateFlow(false)
    fun getStartDestination(): Any = runBlocking {
        if (accountRepository.getAllAccounts().isEmpty()) {
            return@runBlocking Welcome
        } else {
            val first = accountRepository.getCurrentAccount()
            userId.value = first.id.toInt()
            return@runBlocking Parcels(first.id)

        }
    }

    init {
        viewModelScope.launch {
            userId.collect {
                if (it == 0) {
                    return@collect
                }
                clientManager.initSyncClient(it.toLong())
            }
        }
    }

    fun showUserSwitcher() {
        userSwtitcherVisible.value = true
        viewModelScope.launch {
            accounts.value = accountRepository.getAllAccounts()
        }
    }

    fun hideUserSwitcher() {
        userSwtitcherVisible.value = false
    }

    suspend fun switchUser(account: AccountEntity) {
        val oldAccount = accountRepository.getAccountById(userId.value.toLong())
        accountRepository.updateAccount(oldAccount.copy(selected = false).apply { id = oldAccount.id })
        userId.value = account.id.toInt()
        accountRepository.updateAccount(account.copy(selected = true).apply { id = account.id})
        userSwtitcherVisible.value = false
    }
}
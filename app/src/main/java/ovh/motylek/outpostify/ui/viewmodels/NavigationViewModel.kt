package ovh.motylek.outpostify.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.koin.android.annotation.KoinViewModel
import ovh.motylek.outpostify.data.repository.AccountRepository
import ovh.motylek.outpostify.ui.routes.Parcels
import ovh.motylek.outpostify.ui.routes.Welcome

@KoinViewModel
class NavigationViewModel(
    private val accountRepository: AccountRepository
) : ViewModel() {
    val userId = MutableStateFlow(0)
    fun getStartDestination(): Any = runBlocking {
        if (accountRepository.getAllAccounts().isEmpty()) {
            return@runBlocking Welcome
        } else {
            val first = accountRepository.getFirstAccount()
            userId.value = first.id.toInt()
            return@runBlocking Parcels(first.id)

        }
    }
}
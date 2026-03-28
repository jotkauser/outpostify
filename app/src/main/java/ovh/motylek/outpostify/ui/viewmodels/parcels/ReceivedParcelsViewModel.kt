package ovh.motylek.outpostify.ui.viewmodels.parcels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import ovh.motylek.outpostify.api.data.ParcelType
import ovh.motylek.outpostify.data.repository.AccountRepository
import ovh.motylek.outpostify.data.repository.ParcelRepository

@KoinViewModel
class ReceivedParcelsViewModel(
    private val accountRepository: AccountRepository,
    private val parcelRepository: ParcelRepository
) : ViewModel() {
    private val currentAccount = accountRepository.getCurrentAccount()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val isRefreshing = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val parcels = currentAccount
        .filterNotNull()
        .flatMapLatest {
            parcelRepository.getParcels(it, ParcelType.RECEIVED, true)
        }
        .map { p -> p.sortedByDescending { it.events.last().date } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun refresh() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val user = accountRepository.getCurrentAccount().first()!!

            parcelRepository.getParcels(user, ParcelType.RECEIVED, true).first()
        } catch (e: Exception) {
            // TODO: Add error handling
            e.printStackTrace()
        }
    }
}
package ovh.motylek.outpostify.ui.viewmodels.parcels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel
import ovh.motylek.outpostify.api.data.ParcelType
import ovh.motylek.outpostify.data.repository.AccountRepository
import ovh.motylek.outpostify.data.repository.ParcelRepository

@KoinViewModel
class ReceivedParcelsViewModel(
    accountRepository: AccountRepository,
    parcelRepository: ParcelRepository
) : ViewModel() {
    private val currentAccount = accountRepository.getCurrentAccount()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val parcels = currentAccount
        .filterNotNull()
        .flatMapLatest {
            parcelRepository.getParcels(it, ParcelType.RECEIVED, true)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}
package ovh.motylek.outpostify.ui.viewmodels.parcels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import ovh.motylek.outpostify.api.data.ParcelType
import ovh.motylek.outpostify.data.database.entities.ParcelWithEvents
import ovh.motylek.outpostify.data.repository.AccountRepository
import ovh.motylek.outpostify.data.repository.ParcelRepository
import ovh.motylek.outpostify.utils.withCurrentAccount

sealed class ReceivedParcelsUiState {
    data object Loading : ReceivedParcelsUiState()
    data object Empty : ReceivedParcelsUiState()
    data class Success(val parcels: List<ParcelWithEvents>) : ReceivedParcelsUiState()
    data class Error(val error: Throwable) : ReceivedParcelsUiState()
}

@KoinViewModel
class ReceivedParcelsViewModel(
    private val accountRepository: AccountRepository,
    private val parcelRepository: ParcelRepository
) : ViewModel() {

    val state = accountRepository.withCurrentAccount { account ->
        parcelRepository.getParcels(account, ParcelType.RECEIVED, true)
            .map { parcels ->
                val sorted = parcels.sortedByDescending { it.events.lastOrNull()?.date }
                if (sorted.isEmpty()) {
                    ReceivedParcelsUiState.Empty
                } else {
                    ReceivedParcelsUiState.Success(sorted)
                }
            }
    }
    .catch { emit(ReceivedParcelsUiState.Error(it)) }
    .stateIn(viewModelScope, SharingStarted.Eagerly, ReceivedParcelsUiState.Loading)

    val isRefreshing = MutableStateFlow(false)
    val selectedParcel = MutableStateFlow<ParcelWithEvents?>(null)

    fun selectParcel(parcel: ParcelWithEvents?) {
        selectedParcel.value = parcel
    }

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
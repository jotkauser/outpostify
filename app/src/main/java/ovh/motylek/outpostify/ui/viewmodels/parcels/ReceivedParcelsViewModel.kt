package ovh.motylek.outpostify.ui.viewmodels.parcels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel
import ovh.motylek.outpostify.data.repository.ParcelRepository
import ovh.motylek.outpostify.ui.routes.Parcels

@KoinViewModel
class ReceivedParcelsViewModel(
    savedStateHandle: SavedStateHandle,
    parcelRepository: ParcelRepository
) : ViewModel() {
    val args = savedStateHandle.toRoute<Parcels>()
    val parcels = parcelRepository.getParcels(args.userId)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}
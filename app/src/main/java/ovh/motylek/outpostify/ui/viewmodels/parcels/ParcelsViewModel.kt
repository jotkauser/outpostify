package ovh.motylek.outpostify.ui.viewmodels.parcels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.annotation.KoinViewModel
import ovh.motylek.outpostify.api.data.ParcelType
import ovh.motylek.outpostify.data.repository.ParcelRepository
import ovh.motylek.outpostify.ui.routes.Parcels

@KoinViewModel
class ParcelsViewModel(
    private val parcelRepository: ParcelRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val isRefreshing = MutableStateFlow(false)
    val args = savedStateHandle.toRoute<Parcels>()
    suspend fun refreshParcels() {
        isRefreshing.value = true
        parcelRepository.fetchParcels(args.userId, ParcelType.RECEIVED)
        isRefreshing.value = false
    }
}
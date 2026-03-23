package ovh.motylek.outpostify.ui.viewmodels.parcels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.annotation.KoinViewModel
import ovh.motylek.outpostify.api.data.ParcelType
import ovh.motylek.outpostify.data.repository.ParcelRepository

@KoinViewModel
class ParcelsViewModel(
    private val parcelRepository: ParcelRepository,
) : ViewModel() {
    val isRefreshing = MutableStateFlow(false)
    suspend fun refreshParcels() {
        isRefreshing.value = true
        isRefreshing.value = false
    }
}
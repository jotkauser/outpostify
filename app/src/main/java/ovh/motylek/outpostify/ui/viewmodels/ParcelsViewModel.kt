package ovh.motylek.outpostify.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ParcelsViewModel : ViewModel() {
    val isRefreshing = MutableStateFlow(false)

    suspend fun refreshExample() {
        isRefreshing.value = true
        delay(5000)
        isRefreshing.value = false
    }
}
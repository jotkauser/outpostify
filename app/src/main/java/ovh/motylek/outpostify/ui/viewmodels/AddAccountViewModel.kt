package ovh.motylek.outpostify.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class AddAccountViewModel: ViewModel() {
    val phoneNumber = MutableStateFlow("")

    fun setPhoneNumber(phoneNumber: String) {
        if (phoneNumber.length > 9) return
        val allowed = phoneNumber.filter { it.isDigit() }
        this.phoneNumber.value = allowed
    }
}
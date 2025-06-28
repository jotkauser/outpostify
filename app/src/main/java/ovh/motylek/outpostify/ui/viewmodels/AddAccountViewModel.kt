package ovh.motylek.outpostify.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import ovh.motylek.outpostify.api.InPostLoginClient

@KoinViewModel
class AddAccountViewModel: ViewModel() {
    val phoneNumber = MutableStateFlow("")
    val loginClient = InPostLoginClient()
    val showSmsField = MutableStateFlow(false)
    val buttonLoading = MutableStateFlow(false)
    val smsCode = MutableStateFlow("")
    val exception = MutableStateFlow<Throwable?>(null)

    fun setPhoneNumber(phoneNumber: String) {
        if (phoneNumber.length > 9) return
        val allowed = phoneNumber.filter { it.isDigit() }
        this.phoneNumber.value = allowed
    }

    fun setSmsCode(smsCode: String) {
        if (smsCode.length > 6) return
        val allowed = smsCode.filter { it.isDigit() }
        this.smsCode.value = allowed
    }

    fun acceptPhoneNumber() = viewModelScope.launch {
        buttonLoading.value = true
        loginClient.requestSms(phoneNumber.value)
        buttonLoading.value = false
        showSmsField.value = true
    }

    fun acceptSmsCode() = viewModelScope.launch {
        try {
            buttonLoading.value = true
            loginClient.getTokens(phoneNumber.value, smsCode.value)
            buttonLoading.value = false
        } catch (e: Exception) {
            exception.value = e
            buttonLoading.value = false
        }
    }

    fun clearException() {
        exception.value = null
    }
}
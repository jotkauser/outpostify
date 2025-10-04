package ovh.motylek.outpostify.ui.viewmodels

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.annotation.KoinViewModel
import ovh.motylek.outpostify.api.InPostLoginClient
import ovh.motylek.outpostify.api.utils.getJwtExpiration
import ovh.motylek.outpostify.data.database.OutPostifyDatabase
import ovh.motylek.outpostify.data.database.entities.AccountEntity
import ovh.motylek.outpostify.data.repository.AccountRepository

@KoinViewModel
class AddAccountViewModel(
    private val accountsRepository: AccountRepository
): ViewModel() {
    val phoneNumber = MutableStateFlow("")

    val loginClient = InPostLoginClient(
        androidVersion = Build.VERSION.RELEASE,
        deviceModel = Build.MODEL,
        deviceManufacturer = Build.MANUFACTURER,
        deviceCodename = Build.DEVICE
    )

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

    suspend fun acceptSmsCode(): Pair<Boolean, Long> = withContext(Dispatchers.IO) {
        try {
            buttonLoading.value = true
            val tokens = loginClient.getTokens(phoneNumber.value, smsCode.value)
            buttonLoading.value = false
            val account = AccountEntity(
                phoneNumber = phoneNumber.value,
                accessToken = tokens.accessToken,
                refreshToken = tokens.refreshToken,
                tokenExpiration = getJwtExpiration(tokens.accessToken),
            )
            val id = accountsRepository.saveAccount(account)
            account.id = id
            accountsRepository.switchAccount(account)

            return@withContext Pair(true, id)
        } catch (e: Exception) {
            exception.value = e
            buttonLoading.value = false
            return@withContext Pair(false, 0)
        }
    }

    fun clearException() {
        exception.value = null
    }

    fun goBack() {
        showSmsField.value = false
        smsCode.value = ""
    }
}
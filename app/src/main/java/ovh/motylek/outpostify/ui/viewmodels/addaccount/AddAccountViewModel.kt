package ovh.motylek.outpostify.ui.viewmodels.addaccount

import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import org.koin.core.annotation.KoinViewModel
import ovh.motylek.outpostify.api.InPostLoginClient
import androidx.core.net.toUri
import kotlinx.coroutines.flow.MutableStateFlow
import ovh.motylek.outpostify.api.utils.getJwtExpiration
import ovh.motylek.outpostify.api.utils.getJwtPhoneNumber
import ovh.motylek.outpostify.data.database.entities.AccountEntity
import ovh.motylek.outpostify.data.repository.AccountRepository

sealed class AddAccountUiState {
    data object WebViewDisplaying : AddAccountUiState()
    data object Loading : AddAccountUiState()
    data object Success : AddAccountUiState()
    data class Error(val error: Throwable) : AddAccountUiState()
}

@KoinViewModel
class AddAccountViewModel(
    private val accountsRepository: AccountRepository
) : ViewModel() {

    val uiState = MutableStateFlow<AddAccountUiState>(AddAccountUiState.WebViewDisplaying)
    private val loginClient = InPostLoginClient(
        androidVersion = Build.VERSION.RELEASE,
        deviceModel = Build.MODEL,
        deviceManufacturer = Build.MANUFACTURER,
        deviceCodename = Build.DEVICE
    )
    private val oAuthRequest = loginClient.generateOAuthRequest()
    
    fun getOauthUrl() = oAuthRequest.url

    suspend fun handleOauthLogin(callbackUrl: String) {
        uiState.value = AddAccountUiState.Loading
        try {
            val uri = callbackUrl.toUri()
            val code = uri.getQueryParameter("code")
            val state = uri.getQueryParameter("state")
            if (code != null && state != null) {
                val tokens = loginClient.getTokens(code, state, oAuthRequest)
                val account = AccountEntity(
                    phoneNumber = getJwtPhoneNumber(tokens.accessToken),
                    accessToken = tokens.accessToken,
                    refreshToken = tokens.refreshToken,
                    tokenExpiration = getJwtExpiration(tokens.accessToken),
                )
                val id = accountsRepository.saveAccount(account)
                account.id = id
                accountsRepository.switchAccount(account)
            }
            uiState.value = AddAccountUiState.Success
        } catch (e: Throwable) {
            Log.e("AddAccountViewModel", "OAuth login failed", e)
            uiState.value = AddAccountUiState.Error(e)
        }
    }
}
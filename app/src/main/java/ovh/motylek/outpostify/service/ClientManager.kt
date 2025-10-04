package ovh.motylek.outpostify.service

import android.os.Build
import ovh.motylek.outpostify.api.InPostApiClient
import ovh.motylek.outpostify.api.utils.getJwtExpiration
import ovh.motylek.outpostify.data.repository.AccountRepository

class ClientManager(
    private val accountRepository: AccountRepository
) {
    private val apiClients = mutableMapOf<Long, InPostApiClient>()

    suspend fun initApiClient(userId: Long) {
        val user = accountRepository.getAccountById(userId)
        val client = InPostApiClient(
            refreshToken = user.refreshToken,
            accessToken = user.accessToken,

            androidVersion = Build.VERSION.RELEASE,
            deviceModel = Build.MODEL,
            deviceManufacturer = Build.MANUFACTURER,
            deviceCodename = Build.DEVICE
        )
        if (client.isRenewCredentialsNeeded()) {
            val newToken = client.renewCredentials()
            accountRepository.updateAccount(user.copy(accessToken = newToken, tokenExpiration = getJwtExpiration(newToken)).apply { id = user.id })
        }
        apiClients[userId] = client
    }

    suspend fun clientTask(
        userId: Long,
        callback: suspend (InPostApiClient) -> Unit
    ) {
        callback(apiClients[userId]!!)
    }
}
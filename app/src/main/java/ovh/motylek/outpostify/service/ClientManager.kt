package ovh.motylek.outpostify.service

import android.os.Build
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ovh.motylek.outpostify.api.InPostApiClient
import ovh.motylek.outpostify.api.utils.getJwtExpiration
import ovh.motylek.outpostify.data.repository.AccountRepository

class ClientManager(
    private val accountRepository: AccountRepository
) {
    private val mutexes = mutableMapOf<Long, Mutex>()
    private val apiClients = mutableMapOf<Long, InPostApiClient>()

    suspend fun getApiClient(userId: Long): InPostApiClient {
        val mutex = mutexes.computeIfAbsent(userId) { Mutex() }
        return mutex.withLock {
            val account = accountRepository.getAccount(userId).first()
            val client = apiClients.getOrPut(userId) {
                InPostApiClient(
                    refreshToken = account.refreshToken,
                    accessToken = account.accessToken,
                    androidVersion = Build.VERSION.RELEASE,
                    deviceModel = Build.MODEL,
                    deviceManufacturer = Build.MANUFACTURER,
                    deviceCodename = Build.DEVICE
                )
            }
            if (client.isRenewCredentialsNeeded()) {
                try {
                    val newToken = client.renewCredentials()
                    accountRepository.updateAccount(
                        account.copy(
                            accessToken = newToken,
                            tokenExpiration = getJwtExpiration(newToken)
                        )
                    )
                } catch (e: Exception) {
                    throw e
                }
            }
            client
        }
    }
}
package ovh.motylek.outpostify.service

import ovh.motylek.outpostify.api.InPostSyncClient
import ovh.motylek.outpostify.api.utils.getJwtExpiration
import ovh.motylek.outpostify.data.repository.AccountRepository

class ClientManager(
    private val accountRepository: AccountRepository
) {
    private val syncClients = mutableMapOf<Long, InPostSyncClient>()

    suspend fun initSyncClient(userId: Long) {
        val user = accountRepository.getAccountById(userId)
        val client = InPostSyncClient(user.refreshToken, user.accessToken)
        if (client.isRenewCredentialsNeeded()) {
            val newToken = client.renewCredentials()
            accountRepository.updateAccount(user.copy(accessToken = newToken, tokenExpiration = getJwtExpiration(newToken)).apply { id = user.id })
        }
        syncClients[userId] = client
    }

    suspend fun ClientTask(
        userId: Long,
        callback: suspend (InPostSyncClient) -> Unit
    ) {
        callback(syncClients[userId]!!)
    }
}
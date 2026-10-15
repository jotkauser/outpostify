package ovh.motylek.outpostify.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import ovh.motylek.outpostify.data.database.entities.AccountEntity
import ovh.motylek.outpostify.data.repository.AccountRepository

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> AccountRepository.withCurrentAccount(
    block: (account: AccountEntity) -> Flow<T>
): Flow<T> {
    return getCurrentAccount().flatMapLatest { account ->
        account?.let(block) ?: emptyFlow()
    }
}
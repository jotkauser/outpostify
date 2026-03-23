package ovh.motylek.outpostify.data.repository

import kotlinx.coroutines.flow.Flow
import ovh.motylek.outpostify.data.database.dao.AccountDao
import ovh.motylek.outpostify.data.database.entities.AccountEntity

class AccountRepository(
    private val accountDao: AccountDao
) {
    suspend fun saveAccount(accountEntity: AccountEntity): Long = accountDao.insertAndGetId(accountEntity)

    suspend fun getAllAccounts(): List<AccountEntity> = accountDao.getAll()

    suspend fun getFirstAccount(): AccountEntity = accountDao.getFirst()

    fun getCurrentAccount(): Flow<AccountEntity> = accountDao.getSelected()

    suspend fun updateAccount(accountEntity: AccountEntity) = accountDao.update(accountEntity)

    suspend fun getAccount(id: Long): Flow<AccountEntity> = accountDao.getById(id)

    suspend fun switchAccount(accountEntity: AccountEntity)
            = accountDao.switchCurrent(accountEntity.id)
}
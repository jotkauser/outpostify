package ovh.motylek.outpostify.data.repository

import ovh.motylek.outpostify.data.database.dao.AccountDao
import ovh.motylek.outpostify.data.database.entities.AccountEntity

class AccountRepository(
    private val accountDao: AccountDao
) {
    suspend fun saveAccount(accountEntity: AccountEntity): Long = accountDao.insertAndGetId(accountEntity)

    suspend fun getAllAccounts(): List<AccountEntity> = accountDao.getAll()

    suspend fun getFirstAccount(): AccountEntity = accountDao.getFirst()
}
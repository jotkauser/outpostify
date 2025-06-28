package ovh.motylek.outpostify.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ovh.motylek.outpostify.data.database.entities.AccountEntity

@Dao
interface AccountDao : BaseDao<AccountEntity> {
    @Query("SELECT * FROM accounts")
    suspend fun getAll(): List<AccountEntity>
    @Query("SELECT * FROM accounts LIMIT 1")
    suspend fun getFirst(): AccountEntity

    @Insert
    suspend fun insertAndGetId(accountEntity: AccountEntity): Long
}

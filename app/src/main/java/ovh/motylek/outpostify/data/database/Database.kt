package ovh.motylek.outpostify.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ovh.motylek.outpostify.data.database.dao.AccountDao
import ovh.motylek.outpostify.data.database.entities.AccountEntity

@Database(
    version = 1,
    entities = [
        AccountEntity::class
        ],
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class OutPostifyDatabase(): RoomDatabase() {
    abstract fun accountDao(): AccountDao
}
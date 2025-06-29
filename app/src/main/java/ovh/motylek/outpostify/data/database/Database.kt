package ovh.motylek.outpostify.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ovh.motylek.outpostify.data.database.dao.AccountDao
import ovh.motylek.outpostify.data.database.dao.ParcelDao
import ovh.motylek.outpostify.data.database.entities.AccountEntity
import ovh.motylek.outpostify.data.database.entities.ParcelEntity
import ovh.motylek.outpostify.data.database.entities.ParcelEventEntity

@Database(
    version = 1,
    entities = [
        AccountEntity::class,
        ParcelEntity::class,
        ParcelEventEntity::class
        ],
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class OutPostifyDatabase(): RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun parcelDao(): ParcelDao
}
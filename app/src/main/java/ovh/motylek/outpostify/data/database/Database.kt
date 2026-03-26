package ovh.motylek.outpostify.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ovh.motylek.outpostify.data.database.dao.AccountDao
import ovh.motylek.outpostify.data.database.dao.ParcelDao
import ovh.motylek.outpostify.data.database.entities.AccountEntity
import ovh.motylek.outpostify.data.database.entities.ParcelEntity
import ovh.motylek.outpostify.data.database.entities.ParcelEventEntity

@Database(
    version = 4,
    entities = [
        AccountEntity::class,
        ParcelEntity::class,
        ParcelEventEntity::class
    ],
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2
        ),

        AutoMigration(
            from = 2,
            to = 3
        ),

        AutoMigration(
            from = 3,
            to = 4
        ),
    ]
)
@TypeConverters(Converters::class)
abstract class OutPostifyDatabase(): RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun parcelDao(): ParcelDao
}
package ovh.motylek.outpostify.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ovh.motylek.outpostify.data.database.entities.ParcelEntity
import ovh.motylek.outpostify.data.database.entities.ParcelEventEntity
import ovh.motylek.outpostify.data.database.entities.ParcelWithEvents

@Dao
interface ParcelDao : BaseDao<ParcelEntity> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(event: List<ParcelEventEntity>)

    @Insert
    suspend fun insertEvents(event: ParcelEventEntity)

    @Transaction
    @Query("SELECT * FROM parcels WHERE shipmentNumber = :id")
    suspend fun getParcelWithEvents(id: String): ParcelWithEvents

    @Transaction
    @Query("SELECT * FROM parcels")
    fun getAllParcelsWithEvents(): Flow<List<ParcelWithEvents>>

    @Query("DELETE FROM parcels")
    suspend fun deleteAll()

    @Query("DELETE FROM parcel_events")
    suspend fun deleteAllEvents()


    @Transaction
    suspend fun removeAllSaveNew(parcels: List<ParcelEntity>, events: List<ParcelEventEntity>) {
        deleteAll()
        deleteAllEvents()
        insertAll(parcels)
        insertEvents(events)
    }
}
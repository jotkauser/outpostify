package ovh.motylek.outpostify.data.repository

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import ovh.motylek.outpostify.api.data.Parcel
import ovh.motylek.outpostify.api.data.ParcelType
import ovh.motylek.outpostify.data.database.dao.ParcelDao
import ovh.motylek.outpostify.data.database.entities.AccountEntity
import ovh.motylek.outpostify.data.database.mappers.mapToEntities
import ovh.motylek.outpostify.data.database.mappers.mapToParcels
import ovh.motylek.outpostify.data.resource
import ovh.motylek.outpostify.service.ClientManager

class ParcelRepository(
    private val parcelDao: ParcelDao,
    private val clientManager: ClientManager
) {

    private val saveFetchResultMutex = Mutex()

    suspend fun fetchParcels(
        userId: Long,
        type: ParcelType
    ): List<Parcel> {
        val client = clientManager.getApiClient(userId)
        val parcels = when (type) {
            ParcelType.RECEIVED -> client.getTrackedParcels()
            ParcelType.SENT -> TODO()
            ParcelType.RETURNED -> TODO()
        }
        return parcels

    }

    fun getParcels(
        user: AccountEntity,
        type: ParcelType,
        fetch: Boolean? = null
    ) = resource(
        mutex = saveFetchResultMutex,
        shouldFetch = { local ->
            when (fetch) {
                true -> true
                false -> false
                null -> local.isEmpty() //todo: add refresh logic
            }
        },
        fetch = { fetchParcels(user.id, type) },
        query = { parcelDao.getParcelsWithEvents(user.id) },
        saveFetchResult = { old, new ->
            // Szpont
            val entities = new.mapToEntities(user.id)
            parcelDao.removeAllSaveNew(
                parcels = entities.first,
                events = entities.second
            )
        }
    )
}
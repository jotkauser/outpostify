package ovh.motylek.outpostify.data.repository

import kotlinx.coroutines.flow.map
import ovh.motylek.outpostify.api.data.ParcelType
import ovh.motylek.outpostify.data.database.dao.ParcelDao
import ovh.motylek.outpostify.data.database.mappers.mapToEntities
import ovh.motylek.outpostify.data.database.mappers.mapToParcels
import ovh.motylek.outpostify.service.ClientManager

class ParcelRepository(
    private val parcelDao: ParcelDao,
    private val clientManager: ClientManager
) {
    suspend fun fetchParcels(
        userId: Long,
        type: ParcelType
    ) {
        clientManager.clientTask(userId) {
            val parcels = when (type) {
                ParcelType.RECEIVED -> it.getTrackedParcels()
                ParcelType.SENT -> TODO()
                ParcelType.RETURNED -> TODO()
            }
            val mappedParcels = parcels.mapToEntities(userId)
            parcelDao.removeAllSaveNew(mappedParcels.first, mappedParcels.second)
        }

    }

    fun getParcels(userId: Long) = parcelDao.getAllParcelsWithEvents().map { it.mapToParcels(userId) }
}
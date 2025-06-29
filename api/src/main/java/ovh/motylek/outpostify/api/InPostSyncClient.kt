package ovh.motylek.outpostify.api

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ovh.motylek.outpostify.api.data.Parcel
import ovh.motylek.outpostify.api.data.ParcelType
import ovh.motylek.outpostify.api.mappers.mapParcels
import ovh.motylek.outpostify.api.utils.getJwtExpiration

class InPostSyncClient(
    val refreshToken: String,
    val accessToken: String
) {
    private val api = InPostApi(refreshToken, accessToken)

    fun isRenewCredentialsNeeded(): Boolean {
        val tokenExpiration = getJwtExpiration(accessToken)
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return tokenExpiration < now
    }

    suspend fun renewCredentials(): String = api.renewCredentials()

    suspend fun getTrackedParcels(): List<Parcel> = api.getTrackedParcels().mapParcels(ParcelType.RECEIVED)
}